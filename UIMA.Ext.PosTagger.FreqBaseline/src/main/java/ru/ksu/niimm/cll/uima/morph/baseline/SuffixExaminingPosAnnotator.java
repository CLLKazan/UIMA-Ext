/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
abstract class SuffixExaminingPosAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_SUFFIX_LENGTH = "suffixLength";
	public static final String KEY_SUFFIX_LENGTH = "suffixLength";

	// config fields
	@ConfigurationParameter(name = PARAM_SUFFIX_LENGTH)
	protected Integer suffixLength = -1;

	protected String getSuffix(String wordStr) {
		return wordStr.substring(wordStr.length() - suffixLength, wordStr.length());
	}

	protected String makeSuffixKey(String s) {
		return "*" + s;
	}
}