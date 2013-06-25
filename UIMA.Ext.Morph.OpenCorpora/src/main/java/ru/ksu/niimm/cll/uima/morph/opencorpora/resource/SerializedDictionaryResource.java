/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SerializedDictionaryResource implements SharedResourceObject {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(SerializedDictionaryResource.class);

	private MorphDictionary dict;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		try {
			dict = DictionaryDeserializer.from(dr.getInputStream(), String.valueOf(dr.getUrl()));
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	public MorphDictionary getDictionary() {
		return dict;
	}
}