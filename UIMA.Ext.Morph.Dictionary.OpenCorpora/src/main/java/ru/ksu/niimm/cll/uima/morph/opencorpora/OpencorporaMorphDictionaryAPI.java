/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ExternalResourceDescription;

import ru.kfu.itis.cll.uima.util.CachedResourceTuple;
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPI;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedDictionaryDeserializer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedDictionaryDeserializer.GetDictionaryResult;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ConfigurableSerializedDictionaryResource;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DummyWordformPredictor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModelDeserializer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModelResource;

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

	@Override
	public ExternalResourceDescription getGramModelDescription() {
		return createExternalResourceDescription(
				GramModelResource.class,
				DEFAULT_SERIALIZED_DICT_RELATIVE_URL);
	}

	@Override
	public CachedResourceTuple<MorphDictionary> getCachedInstance() throws Exception {
		URL serDictUrl = getSerializedDictionaryURL();
		GetDictionaryResult gdr = CachedDictionaryDeserializer.getInstance().getDictionary(
				serDictUrl, serDictUrl.openStream());
		return new CachedResourceTuple<MorphDictionary>(gdr.cacheKey, gdr.dictionary);
	}

	@Override
	public GramModel getGramModel() throws Exception {
		URL serDictUrl = getSerializedDictionaryURL();
		return GramModelDeserializer.from(serDictUrl.openStream(), serDictUrl.toString());
	}

	private URL getSerializedDictionaryURL() {
		URL serDictUrl;
		try {
			serDictUrl = UIMAFramework.newDefaultResourceManager()
					.resolveRelativePath(DEFAULT_SERIALIZED_DICT_RELATIVE_PATH);
		} catch (MalformedURLException e) {
			// should never happen as the URL is hard-coded here
			throw new IllegalStateException(e);
		}
		if (serDictUrl == null) {
			throw new IllegalStateException(String.format("Can't find %s in UIMA datapath",
					DEFAULT_SERIALIZED_DICT_RELATIVE_PATH));
		}
		return serDictUrl;
	}
}
