/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.uimafit.component.ExternalResourceAware;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.ExternalResourceFactory;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultPOSModelHolder implements OpenNLPModelHolder<POSModel>,
		SharedResourceObject, ExternalResourceAware {

	public static final String RESOURCE_MORPH_DICT = "morphDict";
	// config
	@ConfigurationParameter(name = ExternalResourceFactory.PARAM_RESOURCE_NAME)
	private String resourceName;
	@ExternalResource(key = RESOURCE_MORPH_DICT, mandatory = false)
	private MorphDictionaryHolder morphDictionaryHolder;
	private URL modelUrl;
	// state
	private POSModel model;

	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		if (model != null) {
			throw new IllegalStateException();
		}
		modelUrl = dr.getUrl();
		if (modelUrl == null) {
			throw new IllegalStateException("Can't derive an URL from DataResource");
		}
	}

	@Override
	public POSModel getModel() {
		return model;
	}

	@Override
	public String getResourceName() {
		return resourceName;
	}

	@Override
	public void afterResourcesInitialized() {
		MorphDictionary dict = morphDictionaryHolder == null
				? null
				: morphDictionaryHolder.getDictionary();
		InputStream is = null;
		try {
			is = modelUrl.openStream();
			model = new POSModel(is, dict);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}
