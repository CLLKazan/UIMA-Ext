/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma.Builder;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.Multimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LexemePostProcessorBase implements LemmaPostProcessor {

	@Override
	public boolean process(MorphDictionary dict, Builder lemmaBuilder,
			Multimap<String, Wordform> wfMap) {
		return true;
	}

	@Override
	public void dictionaryParsed(MorphDictionary dict) {
	}

}
