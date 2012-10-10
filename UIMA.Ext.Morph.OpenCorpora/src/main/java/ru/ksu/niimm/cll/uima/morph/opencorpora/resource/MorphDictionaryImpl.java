/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Tables.unmodifiableTable;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.unmodifiableMap;

import java.io.IOException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.LemmaLinkType;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphDictionaryImpl implements Serializable, MorphDictionary {

	private static final long serialVersionUID = -4108756831996589900L;
	private static final Logger log = LoggerFactory.getLogger(MorphDictionaryImpl.class);

	private String version;
	private String revision;
	private Map<String, Grammeme> gramMap = Maps.newHashMap();
	private SortedMap<Integer, Grammeme> numToGram = Maps.newTreeMap();
	private Map<Integer, Lemma> lemmaMap = Maps.newHashMap();
	private Map<Short, LemmaLinkType> lemmaLinkTypeMap = Maps.newHashMap();
	// <from, to, type>
	private Table<Integer, Integer, LemmaLinkType> lemmaLinkTable = TreeBasedTable.create();
	
	private transient Map<BitSet, BitSet> uniqWordformGrammemsMap = Maps.newHashMap();
	private transient Map<BitSet, BitSet> uniqLemmaGrammemsMap = Maps.newHashMap();
	
	private TernarySearchTree<Wordform> wfByString = new TernarySearchTree<Wordform>();;
	// wf indexes
	// by string
	
	// grammem indexes
	// by parent
	private transient Multimap<String, Grammeme> gramByParent;
	private transient BitSet posBits;
	// state mark
	private transient boolean complete = false;

	@Override
	public List<Wordform> getEntries(String str) {
		return ImmutableList.copyOf(wfByString.get(str));
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
		// TODO lock gramMap and numToGram before this method invocation during the parsing
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
		if (gramMap.put(gram.getId(), gram) != null) {
			throw new IllegalStateException(String.format(
					"Duplicate grammem id - %s", gram.getId()));
		}
		if (numToGram.put(gram.getNumId(), gram) != null) {
			throw new IllegalStateException(String.format(
					"Duplicate grammem num id - %s", gram.getNumId()));
		}
	}

	public void addLemma(Lemma l) {
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
		if (!complete) {
			throw new UnsupportedOperationException();
		}
		if (posBits == null) {
			posBits = getGrammemWithChildrenBits("POST", true);
			isTrue(!posBits.isEmpty());
		}
		return (BitSet) posBits.clone();
	}

	@Override
	public BitSet getGrammemWithChildrenBits(String gramId, boolean includeTarget) {
		Grammeme targetGram = getGrammem(gramId);
		BitSet result = new BitSet(getGrammemMaxNumId());
		if (includeTarget) {
			result.set(targetGram.getNumId());
		}
		for (Grammeme childGram : gramByParent.get(gramId)) {
			result.set(childGram.getNumId());
		}
		return result;
	}

	@Override
	public Grammeme getGrammem(String id) {
		Grammeme gr = gramMap.get(id);
		if (gr == null) {
			throw new IllegalStateException(String.format(
					"Unknown grammeme: %s", id));
		}
		return gr;
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
	public BitSet internWordformGrammems(BitSet grammems) {
		if (uniqWordformGrammemsMap.containsKey(grammems)) {
			return uniqWordformGrammemsMap.get(grammems);
		} else {
			uniqWordformGrammemsMap.put(grammems, grammems);
			return grammems;
		}
	}
	
	@Override
	public BitSet internLemmaGrammems(BitSet grammems) {
		if (uniqLemmaGrammemsMap.containsKey(grammems)) {
			return uniqLemmaGrammemsMap.get(grammems);
		} else {
			uniqLemmaGrammemsMap.put(grammems, grammems);
			return grammems;
		}
	}

	public void addWordform(String text, Wordform wf) {
		wfByString.put(text, wf);
	}

	void complete() {
		if (complete) {
			throw new IllegalStateException();
		}
		log.info("Completing dictionary. Valid lemma links: {}, invalid links: {}",
				lemmaLinkTable.size(), invalidLinkCounter);
		log.info("Unique wordform grammem bitsets count: {}", uniqWordformGrammemsMap.size());
		log.info("Unique lemma grammem bitsets count: {}", uniqLemmaGrammemsMap.size());
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
		gramMap = copyOf(gramMap);
		numToGram = ImmutableSortedMap.copyOf(numToGram);
		lemmaMap = unmodifiableMap(lemmaMap);
		lemmaLinkTypeMap = copyOf(lemmaLinkTypeMap);
		lemmaLinkTable = unmodifiableTable(lemmaLinkTable);
	}

	private void buildIndices() {
		log.info("start building indices");
		long timeBefore = currentTimeMillis();
		gramByParent = HashMultimap.create();
		for (Grammeme gr : gramMap.values()) {
			gramByParent.put(gr.getParentId(), gr);
		}
		posBits = null;
		getPosBits();
		log.info("Indices have been built in {} ms", currentTimeMillis() - timeBefore);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		complete = true;
		buildIndices();
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
}