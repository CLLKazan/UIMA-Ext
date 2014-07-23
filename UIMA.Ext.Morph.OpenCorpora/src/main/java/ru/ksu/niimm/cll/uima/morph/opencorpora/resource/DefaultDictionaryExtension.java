/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.Arrays;
import java.util.List;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultDictionaryExtension extends DictionaryExtensionBase {

	@Override
	public List<LemmaPostProcessor> getLexemePostprocessors() {
		return Arrays.<LemmaPostProcessor> asList(
				YoLemmaPostProcessor.INSTANCE);
	}

}
