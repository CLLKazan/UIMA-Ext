/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger;

import static org.uimafit.factory.ConfigurationParameterFactory.createPrimitiveParameter;

import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

/**
 * A class that provides constants and methods to use a pos-tagger.
 * <p>
 * Pos-tagger implementation requires CAS with token annotations (see
 * {@link TokenizerAPI}) and sentence annotations (see
 * {@link SentenceSplitterAPI}).
 * <p>
 * Pos-tagger enriches an input CAS with {@link Word} annotations. Each result
 * {@link Word} should contain at least one {@link Wordform} instance. This
 * {@link Wordform} is supposed to define the most likely interpretation for the
 * underlying token.
 * <p>
 * A pos-tagger implementation is able to reuse {@link Word} annotations that
 * have been existing in an input CAS. To force this parameter '
 * {@value #PARAM_REUSE_EXISTING_WORD_ANNOTATIONS}' should be set to true (this
 * is not default).
 * <p>
 * If a pos-tagger implementation needs an external resource with
 * {@link MorphDictionaryHolder} then this resource should be named '
 * {@value #MORPH_DICTIONARY_RESOURCE_NAME}' and be available among resources
 * managed by the comprising pipeline.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTaggerAPI {

	/**
	 * A name of type-system description that define types produced by
	 * pos-tagger implementations.
	 */
	public static final String TYPESYSTEM_POSTAGGER = "org.opencorpora.morphology-ts";

	/**
	 * A name of analysis engine description that can be imported. An
	 * implementation of pos-tagger should provide its description at this
	 * location either in classpath or UIMA datapath.
	 */
	public static final String AE_POSTAGGER = "ru.kfu.itis.issst.uima.postagger.postagger-ae";

	// parameter names and default values (provided only if a parameter is not mandatory)
	public static final String PARAM_REUSE_EXISTING_WORD_ANNOTATIONS = "reuseExistingWordAnnotations";
	public static final String DEFAULT_REUSE_EXISTING_WORD_ANNOTATIONS = "false";
	/**
	 * a resource name to declare MorphDictionaryHolder implementation
	 */
	public static final String MORPH_DICTIONARY_RESOURCE_NAME = "MorphDictionary";

	/**
	 * @return type-system description instance
	 */
	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_POSTAGGER);
	}

	/**
	 * @return import instance. This is preferred way to include the AE into
	 *         pipeline, especially when a pipeline descriptor is expected to be
	 *         serialized into XML.
	 * @see PipelineDescriptorUtils#createAggregateDescription(java.util.List,
	 *      java.util.List)
	 */
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
