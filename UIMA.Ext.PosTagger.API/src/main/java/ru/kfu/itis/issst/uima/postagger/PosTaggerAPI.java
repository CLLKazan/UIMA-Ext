/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger;

import static org.uimafit.factory.ConfigurationParameterFactory.createPrimitiveParameter;

import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTaggerAPI {

	public static final String TYPESYSTEM_POSTAGGER = "org.opencorpora.morphology-ts";

	public static final String AE_POSTAGGER = "ru.kfu.itis.issst.uima.postagger.postagger-ae";

	// parameter names and default values (provided only if a parameter is not mandatory)
	public static final String PARAM_REUSE_EXISTING_WORD_ANNOTATIONS = "reuseExistingWordAnnotations";
	public static final String DEFAULT_REUSE_EXISTING_WORD_ANNOTATIONS = "false";

	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_POSTAGGER);
	}

	public static Import getAEImport() {
		Import result = new Import_impl();
		result.setName(AE_POSTAGGER);
		return result;
	}

	/**
	 * Convenience method to create the parameter declaration. For example, it
	 * can be used to create parameter overrides in an aggregate descriptor.
	 * 
	 * @return instance with name set to the same value as specified in the
	 *         pos-tagger description.
	 * @see PipelineDescriptorUtils#createOverrideParameterDeclaration(ConfigurationParameter,
	 *      org.apache.uima.analysis_engine.AnalysisEngineDescription, String,
	 *      String)
	 */
	public static ConfigurationParameter createReuseExistingWordAnnotationParameterDeclaration() {
		return createPrimitiveParameter(PARAM_REUSE_EXISTING_WORD_ANNOTATIONS,
				ConfigurationParameter.TYPE_BOOLEAN,
				null, false, false);
	}

	/*
	 * TODO:LOW
	 * 1) This is a wrong place for this method
	 * 2) Also the semantic of this method forces rather rigid constraints on PosTagger output
	 * But until a bright future we have to keep different implementations work in the coherent manner.   
	 */
	public static boolean canCarryWord(Token token) {
		return token instanceof W || token instanceof NUM;
	}

	private PosTaggerAPI() {
	}
}
