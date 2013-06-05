/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.LemmaLinkType;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface MorphDictionary {

	void setWfPredictor(WordformPredictor wfPredictor);

	List<Wordform> getEntries(String str);

	String getVersion();

	String getRevision();

	LemmaLinkType getLemmaLinkType(short id);

	int getGrammemMaxNumId();

	int getGrammemNumId(String gramId);

	Grammeme getGrammem(int numId);

	/**
	 * @param lemmaId
	 * @return lemma with given id
	 * @throws IllegalStateException
	 *             if lemma with given id is not found
	 */
	Lemma getLemma(int lemmaId);

	void addLemma(Lemma lemma);

	String getPos(Lemma lemma);

	BitSet getPosBits();

	/**
	 * @param id
	 * @return grammeme with given string id or null if it does not exist.
	 */
	Grammeme getGrammem(String id);

	Map<Integer, LemmaLinkType> getLemmaOutlinks(int lemmaId);

	Map<Integer, LemmaLinkType> getLemmaInlinks(int lemmaId);

	/**
	 * @param gramId
	 * @param includeTarget
	 *            if true given gramId will be included in result set
	 * @return bitset containing numerical ids of given grammeme children
	 *         (including grandchildren and so on). If grammeme with given
	 *         string id does not exist then result is null.
	 */
	BitSet getGrammemWithChildrenBits(String gramId, boolean includeTarget);

	/**
	 * 
	 * @return grammems whose parent id is null.
	 */
	Set<String> getTopGrammems();

	List<String> toGramSet(BitSet grammems);

	BitSet internWordformGrammems(BitSet src);

	BitSet internLemmaGrammems(BitSet src);
}