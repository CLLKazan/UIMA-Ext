/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * @deprecated Use {@link ConfigurableSerializedDictionaryResource}
 */
@Deprecated
public class SerializedDictionaryResource implements MorphDictionaryHolder {

	private static final Logger log = LoggerFactory.getLogger(SerializedDictionaryResource.class);

	private MorphDictionary dict;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		try {
			dict = DictionaryDeserializer.from(dr.getInputStream(), String.valueOf(dr.getUrl()));
			dict.setWfPredictor(new DummyWordformPredictor(dict));
			log.info("DummyWordformPredictor was set in Deserialized MorphDictionary");
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public MorphDictionary getDictionary() {
		return dict;
	}
}