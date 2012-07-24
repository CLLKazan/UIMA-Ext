/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface LemmaFilter {

	boolean accept(MorphDictionary dict, Lemma lemma);
}