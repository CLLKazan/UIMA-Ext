/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.model.Wordform;
import ru.kfu.itis.issst.uima.morph.model.Lemma.Builder;

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
