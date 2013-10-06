/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.uima.impl.UIMAFramework_impl;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

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
public class CachedSerializedDictionaryResource implements MorphDictionaryHolder {

	private static final Logger log = LoggerFactory
			.getLogger(CachedSerializedDictionaryResource.class);

	// static fields
	private static final WeakHashMap<CacheResourceKey, MorphDictionary> instanceCache = new WeakHashMap<CacheResourceKey, MorphDictionary>();

	// state fields
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
		synchronized (instanceCache) {
			for (Map.Entry<CacheResourceKey, MorphDictionary> cEntry : instanceCache.entrySet()) {
				if (Objects.equal(resUrl, cEntry.getKey().getUrl())) {
					this.cacheKey = cEntry.getKey();
					this.dict = cEntry.getValue();
					log.info("Reusing MorphDictionary instance deserialized from {}", resUrl);
					break;
				}
			}
			if (this.dict == null) {
				try {
					this.cacheKey = new CacheResourceKey(resUrl);
					this.dict = DictionaryDeserializer.from(
							dr.getInputStream(), String.valueOf(resUrl));
					log.info("A wordform predictor has not been set in deserialized MorphDictionary");
					instanceCache.put(this.cacheKey, this.dict);
				} catch (Exception e) {
					throw new ResourceInitializationException(e);
				}
			}
		}
	}

	@Override
	public MorphDictionary getDictionary() {
		return dict;
	}
}