/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.EventListener;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface MorphDictionaryListener extends EventListener {

	void onGramModelSet(MorphDictionary dict);

	void onWordformAdded(MorphDictionary dict, String wfString, Wordform wf);
}
