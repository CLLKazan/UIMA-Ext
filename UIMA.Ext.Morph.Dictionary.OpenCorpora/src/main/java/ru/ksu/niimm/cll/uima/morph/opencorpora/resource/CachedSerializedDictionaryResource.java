/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.net.URL;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedDictionaryDeserializer.GetDictionaryResult;

/**
 * {@link MorphDictionaryHolder} that implements a memory cache to hold
 * references to deserialized {@link MorphDictionary} instances. Cache key is a
 * UIMA data resource URL.
 * <p>
 * Use it with caution! Its primary idea is to avoid heavy memory-leaks when
 * several UIMA-based pipelines run sequentially. The main reason is strong
 * reference map to loggers within {@link UIMAFramework_impl} class while each
 * UIMA logger hold reference to a {@link ResourceManager} instance.
 * </p>
 * 
 * @author Rinat Gareev
 * 
 */
public class CachedSerializedDictionaryResource implements MorphDictionaryHolder, GramModelHolder,
		SharedResourceObject {

	// state fields
	@SuppressWarnings("unused")
	private CacheResourceKey cacheKey;
	private MorphDictionary dict;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(DataResource dr) throws ResourceInitializationException {
		if (this.dict != null) {
			throw new IllegalStateException(
					"Repeated SharedResourceObjectInvocation#load invocation");
		}
		URL resUrl = dr.getUrl();
		try {
			CachedDictionaryDeserializer deser = CachedDictionaryDeserializer.getInstance();
			GetDictionaryResult deserResult = deser.getDictionary(resUrl, dr.getInputStream());
			this.cacheKey = deserResult.cacheKey;
			this.dict = deserResult.dictionary;
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public MorphDictionary getDictionary() {
		return dict;
	}

	@Override
	public GramModel getGramModel() {
		return dict.getGramModel();
	}

}