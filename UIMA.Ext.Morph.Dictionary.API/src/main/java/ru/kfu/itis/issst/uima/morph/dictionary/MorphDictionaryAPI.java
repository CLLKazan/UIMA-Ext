/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary;

import org.apache.uima.resource.ExternalResourceDescription;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphDictionaryAPI {

	public static ExternalResourceDescription getResourceDescriptionForCachedInstance() {
		// XXX
		throw new UnsupportedOperationException("TODO");
		/*
		return createExternalResourceDescription(
				CachedSerializedDictionaryResource.class,
				"file:dict.opcorpora.ser");
				*/
	}

	public static ExternalResourceDescription getResourceDescriptionWithPredictorEnabled() {
		// XXX
		throw new UnsupportedOperationException("TODO");
		/*
		 createExternalResourceDescription(
				ConfigurableSerializedDictionaryResource.class, MORPH_DICT_URL,
				ConfigurableSerializedDictionaryResource.PARAM_PREDICTOR_CLASS_NAME,
				DummyWordformPredictor.class.getName())
		 */
	}

	private MorphDictionaryAPI() {
	}
}
