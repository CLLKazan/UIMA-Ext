/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GramModelResource implements GramModelHolder, SharedResourceObject {

	private GramModel gramModel;

	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		try {
			gramModel = GramModelDeserializer
					.from(dr.getInputStream(), String.valueOf(dr.getUrl()));
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public GramModel getGramModel() {
		return gramModel;
	}

}
