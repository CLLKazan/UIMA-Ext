/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;

/**
 * For test purposes ONLY!
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OddLemmaFilter implements LemmaFilter {

	@Override
	public boolean accept(MorphDictionary dict, Lemma lemma) {
		return lemma.getId() % 10 == 0;
	}

}