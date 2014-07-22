/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import java.util.BitSet;
import java.util.Collection;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

/**
 * @author Rinat Gareev
 * 
 */
public interface AnnotationAdapter {

	void init(MorphDictionary dict);

	void apply(JCas jcas, Annotation token, Collection<Wordform> wordforms);

	/**
	 * @param jcas
	 * @param token
	 * @param lexemeId
	 * @param lemma
	 *            normal form of lexeme. Must be null if lexemeId is specified.
	 * @param posBits
	 */
	void apply(JCas jcas, Annotation token, Integer lexemeId, String lemma, BitSet posBits);
}