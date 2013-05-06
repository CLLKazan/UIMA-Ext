/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Multimap;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

/**
 * For test purposes ONLY!
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OddLemmaFilter implements LemmaPostProcessor {

	@Override
	public boolean process(MorphDictionary dict, Lemma lemma, Multimap<String, Wordform> wfMap) {
		return lemma.getId() % 10 == 0;
	}

}