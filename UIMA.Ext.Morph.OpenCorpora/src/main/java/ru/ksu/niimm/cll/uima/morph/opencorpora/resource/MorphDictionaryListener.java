/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.EventListener;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface MorphDictionaryListener extends EventListener {

	void onGramModelSet(MorphDictionary dict);

	void onWordformAdded(MorphDictionary dict, String wfString, Wordform wf);
}
