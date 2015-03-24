/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.lemmatizer;

import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.opencorpora.cas.Wordform;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

/**
 * A class that provides constants and methods to use a lemmatizer.
 * <p>
 * Lemmatizer implementation requires a CAS with word annotations, i.e., it
 * should be pre-processed by a pos-tagger (see {@link PosTaggerAPI}).
 * <p>
 * Lemmatizer should set 'lemma' feature of each {@link Wordform}s of an input
 * CAS.
 * <p>
 * If a lemmatizer implementation needs an external resource with
 * {@link MorphDictionaryHolder} then this resource should be named '
 * {@value #MORPH_DICTIONARY_RESOURCE_NAME}' and be available among resources
 * managed by the comprising pipeline.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LemmatizerAPI {

	/**
	 * A name of type-system description that define types of annotations that
	 * are affected by lemmatizer.
	 */
	public static final String TYPESYSTEM_LEMMATIZER = "org.opencorpora.morphology-ts";

	/**
	 * A name of analysis engine description that can be imported. An
	 * implementation of lemmatizer should provide its description at this
	 * location either in classpath or UIMA datapath.
	 */
	public static final String AE_LEMMATIZER = "ru.kfu.itis.issst.uima.morph.lemmatizer.lemmatizer-ae";

	/**
	 * a resource name to declare MorphDictionaryHolder implementation
	 */
	public static final String MORPH_DICTIONARY_RESOURCE_NAME =
			PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME;

	/**
	 * @return type-system description instance
	 */
	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_LEMMATIZER);
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
		result.setName(AE_LEMMATIZER);
		return result;
	}

	private LemmatizerAPI() {
	}
}
