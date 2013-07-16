/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

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

}