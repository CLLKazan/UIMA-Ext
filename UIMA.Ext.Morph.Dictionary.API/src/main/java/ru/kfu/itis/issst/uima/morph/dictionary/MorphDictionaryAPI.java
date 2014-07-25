/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary;

import org.apache.uima.resource.ExternalResourceDescription;

import ru.kfu.itis.cll.uima.util.CachedResourceTuple;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface MorphDictionaryAPI {

	public ExternalResourceDescription getResourceDescriptionForCachedInstance();

	public ExternalResourceDescription getResourceDescriptionWithPredictorEnabled();

	public ExternalResourceDescription getGramModelDescription();

	public CachedResourceTuple<MorphDictionary> getCachedInstance() throws Exception;

	public GramModel getGramModel() throws Exception;
}
