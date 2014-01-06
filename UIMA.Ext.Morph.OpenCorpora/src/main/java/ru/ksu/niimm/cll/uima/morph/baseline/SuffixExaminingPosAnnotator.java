/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
abstract class SuffixExaminingPosAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_SUFFIX_LENGTH = "suffixLength";
	public static final String KEY_SUFFIX_LENGTH = "suffixLength";
	public static final String RESOURCE_MORPH_DICTIONARY = "MorphDictionary";

	// config fields
	@ConfigurationParameter(name = PARAM_SUFFIX_LENGTH)
	protected Integer suffixLength = -1;
	@ExternalResource(key = RESOURCE_MORPH_DICTIONARY, mandatory = true)
	private MorphDictionaryHolder dictHolder;
	protected MorphDictionary dict;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		dict = dictHolder.getDictionary();
	}

	protected String getSuffix(String wordStr) {
		return wordStr.substring(wordStr.length() - suffixLength, wordStr.length());
	}

	protected String makeSuffixKey(String s) {
		return "*" + s;
	}
}