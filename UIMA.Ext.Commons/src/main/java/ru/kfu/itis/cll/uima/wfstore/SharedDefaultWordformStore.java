/**
 * 
 */
package ru.kfu.itis.cll.uima.wfstore;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SharedDefaultWordformStore<TagType> extends DefaultWordformStore<TagType> implements
		SharedResourceObject {

	private static final long serialVersionUID = 7266695078951639418L;

	@SuppressWarnings("unchecked")
	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		DefaultWordformStore<TagType> ws;
		try {
			ws = (DefaultWordformStore<TagType>) SerializationUtils.deserialize(
					new BufferedInputStream(dr.getInputStream()));
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		this.strKeyMap = ws.strKeyMap;
		this.metadataMap = ws.metadataMap;
	}

}