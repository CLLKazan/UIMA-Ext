/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import com.google.common.collect.Multimap;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.model.Lemma;
import ru.kfu.itis.issst.uima.morph.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface LemmaPostProcessor {

	boolean process(MorphDictionary dict, Lemma.Builder lemmaBuilder,
			Multimap<String, Wordform> wfMap);
	
	void dictionaryParsed(MorphDictionary dict);
}