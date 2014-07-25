/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary.resource;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import ru.kfu.itis.issst.uima.morph.model.Lemma;
import ru.kfu.itis.issst.uima.morph.model.LemmaLinkType;
import ru.kfu.itis.issst.uima.morph.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface MorphDictionary {

	String getVersion();

	String getRevision();

	GramModel getGramModel();

	List<Wordform> getEntries(String str);

	LemmaLinkType getLemmaLinkType(short id);

	/**
	 * @param lemmaId
	 * @return lemma with given id
	 * @throws IllegalStateException
	 *             if lemma with given id is not found
	 */
	Lemma getLemma(int lemmaId);

	// TODO move to a interface that is kind of MutableMorphDictionary 
	void addLemma(Lemma lemma);

	int getLemmaMaxId();

	Map<Integer, LemmaLinkType> getLemmaOutlinks(int lemmaId);

	Map<Integer, LemmaLinkType> getLemmaInlinks(int lemmaId);

	/**
	 * @param tag
	 * @return true if this dictionary has the given tag
	 */
	boolean containsGramSet(BitSet tag);
}
