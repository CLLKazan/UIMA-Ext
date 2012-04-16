/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.seman;

import java.io.File;
import java.util.Collections;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import ru.aot.morph.JavaMorphAPI;

/**
 * @author Rinat Gareev
 * 
 */
public class JNIMorphLibManager implements SharedResourceObject {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(DataResource dataRes) throws ResourceInitializationException {
		File libFile = new File(dataRes.getUri());
		if (!JavaMorphAPI.isLibraryLoaded()) {
			JavaMorphAPI.loadLibrary(libFile.getPath());
		} else {
			throw new ResourceInitializationException(new IllegalStateException(
					"JavaMorphAPI library has already been loaded. Reload is not possible."));
		}
		JavaMorphAPI.initDictionaries(Collections
				.singleton(JavaMorphAPI.Language.Russian));
	}

}