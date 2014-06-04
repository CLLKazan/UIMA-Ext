/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.InvalidFormatException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultPOSModelHolder implements SharedResourceObject, OpenNLPModelHolder<POSModel> {

	private POSModel model;

	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		if (model != null) {
			throw new IllegalStateException();
		}
		try {
			InputStream is = dr.getInputStream();
			if (is == null) {
				throw new IllegalStateException("Can't get InputStream for resource initialization");
			}
			try {
				model = new POSModel(is);
			} finally {
				IOUtils.closeQuietly(is);
			}
		} catch (InvalidFormatException e) {
			throw new ResourceInitializationException(e);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public POSModel getModel() {
		return model;
	}

}
