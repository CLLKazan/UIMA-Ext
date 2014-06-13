/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Multimap;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface LemmaPostProcessor {

	boolean process(MorphDictionary dict, Lemma.Builder lemmaBuilder,
			Multimap<String, Wordform> wfMap);
}