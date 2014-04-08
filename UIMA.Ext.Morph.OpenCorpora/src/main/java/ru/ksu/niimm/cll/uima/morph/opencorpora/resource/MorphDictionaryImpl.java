/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Tables.unmodifiableTable;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.LemmaLinkType;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphDictionaryImpl implements Serializable, MorphDictionary {

	private static final long serialVersionUID = -5575575933753079145L;
	private static final Logger log = LoggerFactory.getLogger(MorphDictionaryImpl.class);

	// meta fields
	private String version;
	private String revision;
	// gram set fields
	private Map<String, Grammeme> gramMap = Maps.newHashMap();
	private SortedMap<Integer, Grammeme> numToGram = Maps.newTreeMap();
	private boolean gramSetLocked;
	// grammem indexes
	private Multimap<String, Grammeme> gramByParent;
	private BitSet posBits;
	// other fields
	private Map<Integer, Lemma> lemmaMap = Maps.newHashMap();
	private Map<Short, LemmaLinkType> lemmaLinkTypeMap = Maps.newHashMap();
	// <from, to, type>
	private Table<Integer, Integer, LemmaLinkType> lemmaLinkTable = TreeBasedTable.create();

	private Map<BitSet, BitSet> uniqWordformGrammemsMap = Maps.newHashMap();
	private Map<BitSet, BitSet> uniqLemmaGrammemsMap = Maps.newHashMap();

	private WordformTST wfByString = new WordformTST();
	// set of complete tags (lex + wordform) seen in stored wordforms
	private Set<BitSet> tagset = Sets.newHashSet();

	private transient WordformPredictor wfPredictor;

	// state mark
	private transient boolean complete = false;

	@Override
	public void setWfPredictor(WordformPredictor wfPredictor) {
		this.wfPredictor = wfPredictor;
	}

	@Override
	public List<Wordform> getEntries(String str) {
		WordformTSTSearchResult result = wfByString.getLongestPrefixMatch(str);
		if (result.isMatchExact())
			return Lists.newArrayList(result);
		else if (wfPredictor != null) {
			return ImmutableList.copyOf(wfPredictor.predict(str, result));
		} else {
			// wfPredictor is not set
			return ImmutableList.of();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getVersion() {
		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRevision() {
		return revision;
	}

	@Override
	public int getGrammemMaxNumId() {
		if (!gramSetLocked) {
			throw new IllegalStateException("gramSet is not locked");
		}
		return numToGram.lastKey();
	}

	@Override
	public int getGrammemNumId(String gramId) {
		Grammeme gr = gramMap.get(gramId);
		if (gr == null) {
			noGrammem(gramId);
		}
		return gr.getNumId();
	}

	@Override
	public Grammeme getGrammem(int numId) {
		return numToGram.get(numId);
	}

	public void addGrammeme(Grammeme gram) {
		if (gramSetLocked) {
			throw new IllegalStateException("Gram set was locked");
		}
		if (gramMap.put(gram.getId(), gram) != null) {
			throw new IllegalStateException(String.format(
					"Duplicate grammem id - %s", gram.getId()));
		}
		if (numToGram.put(gram.getNumId(), gram) != null) {
			throw new IllegalStateException(String.format(
					"Duplicate grammem num id - %s", gram.getNumId()));
		}
	}

	@Override
	public synchronized void addLemma(Lemma l) {
		l = l.cloneWithGrammems(internLemmaGrammems(l.getGrammems()));
		if (lemmaMap.put(l.getId(), l) != null) {
			throw new IllegalStateException(String.format(
					"Duplicate lemma id - %s", l.getId()));
		}
	}

	public void addLemmaLinkType(LemmaLinkType linkType) {
		if (lemmaLinkTypeMap.put(linkType.getId(), linkType) != null) {
			throw new IllegalStateException(String.format(
					"Duplicate lemma link type - %s", linkType.getId()));
		}
	}

	private transient int invalidLinkCounter = 0;

	public void addLemmaLink(int from, int to, short linkTypeId) {
		if (!lemmaMap.containsKey(from)) {
			invalidLinkCounter++;
			return;
		}
		if (!lemmaMap.containsKey(to)) {
			invalidLinkCounter++;
			return;
		}
		LemmaLinkType linkType = getLemmaLinkType(linkTypeId);
		if (linkType == null) {
			noLemmaLinkType(linkTypeId);
		}
		lemmaLinkTable.put(from, to, linkType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LemmaLinkType getLemmaLinkType(short id) {
		return lemmaLinkTypeMap.get(id);
	}

	@Override
	public Lemma getLemma(int lemmaId) {
		Lemma result = lemmaMap.get(lemmaId);
		if (result == null) {
			throw new IllegalStateException(String.format(
					"No lemma with id = %s", lemmaId));
		}
		return result;
	}

	@Override
	public String getPos(Lemma lemma) {
		BitSet lGrams = lemma.getGrammems();
		lGrams.and(getPosBits());
		if (lGrams.isEmpty()) {
			log.error("{} does not have POS grammem", lemma);
			return null;
		}
		if (lGrams.cardinality() > 1) {
			List<String> posList = Lists.newLinkedList();
			for (int i = lGrams.nextSetBit(0); i >= 0; i = lGrams.nextSetBit(i + 1)) {
				posList.add(getGrammem(i).getId());
			}
			log.error("{} has more than 1 POS grammem:\n{}\n" +
					"Will return first w.r.t. numerical id", lemma, posList);
		}
		int gramNumId = lGrams.nextSetBit(0);
		Grammeme result = getGrammem(gramNumId);
		notNull(result);
		return result.getId();
	}

	@Override
	public BitSet getPosBits() {
		return (BitSet) posBits.clone();
	}

	@Override
	public BitSet getGrammemWithChildrenBits(String gramId, boolean includeTarget) {
		Grammeme targetGram = getGrammem(gramId);
		if (targetGram == null) {
			return null;
		}
		return getGrammemWithChildrenBits(targetGram, includeTarget);
	}

	private BitSet getGrammemWithChildrenBits(Grammeme gram, boolean includeTarget) {
		BitSet result = new BitSet(getGrammemMaxNumId());
		if (includeTarget) {
			result.set(gram.getNumId());
		}
		for (Grammeme childGram : gramByParent.get(gram.getId())) {
			result.or(getGrammemWithChildrenBits(childGram, true));
		}
		return result;
	}

	@Override
	public Set<String> getTopGrammems() {
		ImmutableSet.Builder<String> resultBuilder = ImmutableSet.builder();
		for (Grammeme gr : gramByParent.get(null)) {
			resultBuilder.add(gr.getId());
		}
		return resultBuilder.build();
	}

	@Override
	public Grammeme getGrammem(String id) {
		return gramMap.get(id);
	}

	@Override
	public List<String> toGramSet(BitSet gramBits) {
		ImmutableList.Builder<String> rb = ImmutableList.builder();
		for (int i = gramBits.nextSetBit(0); i >= 0; i = gramBits.nextSetBit(i + 1)) {
			rb.add(getGrammem(i).getId());
		}
		return rb.build();
	}

	@Override
	public boolean containsGramSet(BitSet tag) {
		return tagset.contains(tag);
	}

	@Override
	public Map<Integer, LemmaLinkType> getLemmaOutlinks(int lemmaId) {
		return lemmaLinkTable.row(lemmaId);
	}

	@Override
	public Map<Integer, LemmaLinkType> getLemmaInlinks(int lemmaId) {
		return lemmaLinkTable.column(lemmaId);
	}

	public void addWordform(String text, Wordform wf) {
		wf = wf.cloneWithGrammems(internWordformGrammems(wf.getGrammems()));
		wfByString.put(text, wf);
		// add complete tag
		BitSet tag = wf.getGrammems();
		tag.or(getLemma(wf.getLemmaId()).getGrammems());
		tagset.add(tag);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO remove
		log.info("{}#finalize...", this);
	}

	void completeGramSet() {
		if (!gramSetLocked) {
			gramMap = copyOf(gramMap);
			numToGram = ImmutableSortedMap.copyOf(numToGram);
			gramSetLocked = true;
			log.info("Grammeme set has been locked");
			// build indices 
			gramByParent = HashMultimap.create();
			for (Grammeme gr : gramMap.values()) {
				gramByParent.put(gr.getParentId(), gr);
			}
			// 
			posBits = getGrammemWithChildrenBits("POST", true);
			isTrue(!posBits.isEmpty());
			log.info("Grammeme indices have been built");
		}
	}

	void complete() {
		if (complete) {
			throw new IllegalStateException();
		}
		completeGramSet();
		log.info("Completing dictionary. Valid lemma links: {}, invalid links: {}",
				lemmaLinkTable.size(), invalidLinkCounter);
		log.info("Unique wordform grammem bitsets count: {}", uniqWordformGrammemsMap.size());
		log.info("Unique lemma grammem bitsets count: {}", uniqLemmaGrammemsMap.size());
		log.info("Unique tag bitset count: {}", tagset.size());
		makeUnmodifiable();
		//		uniqGrammemsMap = null;
		complete = true;
	}

	void setVersion(String version) {
		this.version = version;
	}

	void setRevision(String revision) {
		this.revision = revision;
	}

	@SuppressWarnings("unused")
	private void noLemma(int id) {
		throw new IllegalStateException(String.format(
				"Lemma with id = %s is not registered", id));
	}

	private void noLemmaLinkType(int id) {
		throw new IllegalStateException(String.format(
				"Lemma link type with id = %s is not registered", id));
	}

	private void noGrammem(String id) {
		throw new IllegalStateException(String.format(
				"Grammem with id = %s is not registered", id));
	}

	private void makeUnmodifiable() {
		//	lemmaMap = unmodifiableMap(lemmaMap);
		// ??? tagset
		lemmaLinkTypeMap = copyOf(lemmaLinkTypeMap);
		lemmaLinkTable = unmodifiableTable(lemmaLinkTable);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		complete = true;
	}

	private static void notNull(Object obj) {
		if (obj == null) {
			throw new IllegalStateException("Unexpected null value");
		}
	}

	private static void isTrue(boolean condition) {
		if (!condition) {
			throw new IllegalStateException("Assertion failed");
		}
	}

	private BitSet internWordformGrammems(BitSet grammems) {
		if (uniqWordformGrammemsMap.containsKey(grammems)) {
			return uniqWordformGrammemsMap.get(grammems);
		} else {
			uniqWordformGrammemsMap.put(grammems, grammems);
			return grammems;
		}
	}

	private BitSet internLemmaGrammems(BitSet grammems) {
		if (uniqLemmaGrammemsMap.containsKey(grammems)) {
			return uniqLemmaGrammemsMap.get(grammems);
		} else {
			uniqLemmaGrammemsMap.put(grammems, grammems);
			return grammems;
		}
	}
}
