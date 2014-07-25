/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Multimap;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.model.Lemma;
import ru.kfu.itis.issst.uima.morph.model.Wordform;

/**
 * For test purposes ONLY!
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OddLemmaFilter extends LexemePostProcessorBase {

	@Override
	public boolean process(MorphDictionary dict, Lemma.Builder lemma,
			Multimap<String, Wordform> wfMap) {
		return lemma.getLemmaId() % 10 == 0;
	}

}