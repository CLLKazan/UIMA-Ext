/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import org.apache.uima.resource.ExternalResourceDescription;

import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPI;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ConfigurableSerializedDictionaryResource;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DummyWordformPredictor;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OpencorporaMorphDictionaryAPI implements MorphDictionaryAPI {

	public static final String DEFAULT_SERIALIZED_DICT_RELATIVE_PATH = "dict.opcorpora.ser";
	public static final String DEFAULT_SERIALIZED_DICT_RELATIVE_URL =
			"file:" + DEFAULT_SERIALIZED_DICT_RELATIVE_PATH;

	@Override
	public ExternalResourceDescription getResourceDescriptionForCachedInstance() {
		return createExternalResourceDescription(
				CachedSerializedDictionaryResource.class,
				DEFAULT_SERIALIZED_DICT_RELATIVE_URL);
	}

	@Override
	public ExternalResourceDescription getResourceDescriptionWithPredictorEnabled() {
		return createExternalResourceDescription(
				ConfigurableSerializedDictionaryResource.class,
				DEFAULT_SERIALIZED_DICT_RELATIVE_URL,
				ConfigurableSerializedDictionaryResource.PARAM_PREDICTOR_CLASS_NAME,
				DummyWordformPredictor.class.getName());
	}
}
