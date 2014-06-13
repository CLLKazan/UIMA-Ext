/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.List;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryExtensionBase implements DictionaryExtension {

	@Override
	public List<LemmaPostProcessor> getLexemePostprocessors() {
		return null;
	}

	@Override
	public List<GramModelPostProcessor> getGramModelPostProcessors() {
		return null;
	}

}
