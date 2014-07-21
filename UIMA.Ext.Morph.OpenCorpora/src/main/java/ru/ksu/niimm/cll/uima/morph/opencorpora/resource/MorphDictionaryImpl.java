/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.google.common.collect.Tables.unmodifiableTable;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.LemmaLinkType;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
	//
	private GramModel gramModel;
	//
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

	private transient EventListenerSupport<MorphDictionaryListener> listeners = EventListenerSupport
			.create(MorphDictionaryListener.class);

	@Override
	public GramModel getGramModel() {
		return gramModel;
	}

	public void setGramModel(GramModel gramModel) {
		if (this.gramModel != null) {
			throw new UnsupportedOperationException("Can't change a grammatical model");
		}
		this.gramModel = gramModel;
		// fire event
		listeners.fire().onGramModelSet(this);
	}

	public void addListener(MorphDictionaryListener listener) {
		listeners.addListener(listener);
	}

	public void removeListener(MorphDictionaryListener listener) {
		listeners.removeListener(listener);
	}

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
	public synchronized void addLemma(Lemma l) {
		l = l.cloneWithGrammems(internLemmaGrammems(l.getGrammems()));
		if (lemmaMap.put(l.getId(), l) != null) {
			throw new IllegalStateException(String.format(
					"Duplicate lemma id - %s", l.getId()));
		}
	}

	@Override
	public int getLemmaMaxId() {
		int max = Integer.MIN_VALUE;
		for (Integer curId : lemmaMap.keySet()) {
			if (curId == null) {
				continue;
			}
			if (curId > max) {
				max = curId;
			}
		}
		return max;
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
		// fire event
		listeners.fire().onWordformAdded(this, text, wf);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO remove
		log.info("{}#finalize...", this);
	}

	void complete() {
		if (complete) {
			throw new IllegalStateException();
		}
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
