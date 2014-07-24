/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary;

import org.apache.uima.resource.ExternalResourceDescription;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface MorphDictionaryAPI {

	public ExternalResourceDescription getResourceDescriptionForCachedInstance();

	public ExternalResourceDescription getResourceDescriptionWithPredictorEnabled();
	
}
