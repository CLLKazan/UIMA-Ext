/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.lemmatizer;

import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LemmatizerAPI {

	public static final String TYPESYSTEM_LEMMATIZER = "org.opencorpora.morphology-ts";

	public static final String AE_LEMMATIZER = "ru.kfu.itis.issst.uima.morph.lemmatizer.lemmatizer-ae";

	/**
	 * a resource name to declare MorphDictionaryHolder implementation
	 */
	public static final String MORPH_DICTIONARY_RESOURCE_NAME =
			PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME;

	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_LEMMATIZER);
	}

	public static Import getAEImport() {
		Import result = new Import_impl();
		result.setName(AE_LEMMATIZER);
		return result;
	}

	private LemmatizerAPI() {
	}
}
