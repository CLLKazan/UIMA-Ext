/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static java.lang.System.currentTimeMillis;

import java.io.ObjectInputStream;

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

	private static final Logger log = LoggerFactory.getLogger(SerializedDictionaryResource.class);

	private MorphDictionary dict;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		log.info("About to deserialize MorphDictionary...");
		try {
			long timeBefore = currentTimeMillis();
			ObjectInputStream ois = new ObjectInputStream(dr.getInputStream());
			try {
				dict = (MorphDictionary) ois.readObject();
			} finally {
				ois.close();
			}
			log.info("Deserialization of MorphDictionary finished in {} ms",
					currentTimeMillis() - timeBefore);
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	public MorphDictionary getDictionary() {
		return dict;
	}
}