/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary;

import org.apache.uima.resource.ExternalResourceDescription;

import ru.kfu.itis.cll.uima.util.CachedResourceTuple;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;

/**
 * An interface to get external resource descriptions that implements
 * {@link MorphDictionaryHolder} and {@link GramModelHolder}. It also provides
 * methods to retrieve actual instances of these resources.
 * <p>
 * To obtain available default implementation of this interface use
 * {@link MorphDictionaryAPIFactory#getMorphDictionaryAPI()}.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface MorphDictionaryAPI {

	/**
	 * Provides a description for the UIMA external resource that wraps cached
	 * instance of {@link MorphDictionary}.
	 * 
	 * @return new instance of resource description. So it is allowed to change
	 *         name of the description without affecting originators of previous
	 *         invocations.
	 */
	public ExternalResourceDescription getResourceDescriptionForCachedInstance();

	/**
	 * Provides a description for the UIMA external resource that wraps a
	 * {@link MorphDictionary} instance with a prediction capability. I.e., this
	 * dictionary instance produces interpretations for an input that is not in
	 * a source of the dictionary.
	 * 
	 * @return new description instance.
	 */
	public ExternalResourceDescription getResourceDescriptionWithPredictorEnabled();

	/**
	 * Provides a description for the UIMA external resource that wrap a
	 * {@link GramModel} instance underlying the {@link MorphDictionary}
	 * instance managed by this API.
	 * 
	 * @return new description instance.
	 */
	public ExternalResourceDescription getGramModelDescription();

	/**
	 * Provides direct access to {@link MorphDictionary} instance.
	 * 
	 * @return tuple (cacheKey, dictionary instance). The purpose of cache keys:
	 *         if all cache keys produces by this API are collected by JVM
	 *         Garbage Collector, the cached {@link MorphDictionary} instance
	 *         will also be collected.
	 * @throws Exception
	 */
	public CachedResourceTuple<MorphDictionary> getCachedInstance() throws Exception;

	/**
	 * Provides direct access to {@link GramModel} instance.
	 * 
	 * @throws Exception
	 */
	public GramModel getGramModel() throws Exception;
}
