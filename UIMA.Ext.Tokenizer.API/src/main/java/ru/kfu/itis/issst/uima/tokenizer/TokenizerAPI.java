/**
 * 
 */
package ru.kfu.itis.issst.uima.tokenizer;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TokenizerAPI {

	public static final String AE_TOKENIZER = "ru.kfu.itis.issst.uima.tokenizer.tokenizer-ae";

	public static final String TYPESYSTEM_TOKENIZER = "ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem";

	/**
	 * A parameter of tokenizer AE that defines types of annotations which
	 * covered texts must be tokenized.
	 */
	public static final String PARAM_SPAN_TYPE = "spanType";

	/**
	 * Default value of {@link #PARAM_SPAN_TYPE}
	 */
	public static final String DEFAULT_SPAN_TYPE = "uima.tcas.DocumentAnnotation";

	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_TOKENIZER);
	}

	public static AnalysisEngineDescription getAEDescription() throws UIMAException, IOException {
		return AnalysisEngineFactory.createAnalysisEngineDescription(AE_TOKENIZER);
	}

	public static Import getAEImport() {
		Import result = new Import_impl();
		result.setName(AE_TOKENIZER);
		return result;
	}

	private TokenizerAPI() {
	}
}
