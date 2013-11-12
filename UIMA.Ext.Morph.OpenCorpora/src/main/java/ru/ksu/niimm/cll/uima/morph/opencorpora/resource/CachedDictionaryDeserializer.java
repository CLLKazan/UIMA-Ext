/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CachedDictionaryDeserializer {

	private static final CachedDictionaryDeserializer instance = new CachedDictionaryDeserializer();

	public static CachedDictionaryDeserializer getInstance() {
		return instance;
	}

	public static class GetDictionaryResult {
		public final CacheResourceKey cacheKey;
		public final MorphDictionary dictionary;

		public GetDictionaryResult(CacheResourceKey cacheKey, MorphDictionary dictionary) {
			this.cacheKey = cacheKey;
			this.dictionary = dictionary;
		}
	}

	// config fields
	private final Logger log = LoggerFactory.getLogger(getClass());
	// state fields
	private final WeakHashMap<CacheResourceKey, MorphDictionary> instanceCache = new WeakHashMap<CacheResourceKey, MorphDictionary>();

	private CachedDictionaryDeserializer() {
	}

	public GetDictionaryResult getDictionary(URL url, InputStream in) throws Exception {
		CacheResourceKey cacheKey = null;
		MorphDictionary dictionary = null;
		synchronized (instanceCache) {
			for (Map.Entry<CacheResourceKey, MorphDictionary> cEntry : instanceCache.entrySet()) {
				if (Objects.equal(url, cEntry.getKey().getUrl())) {
					cacheKey = cEntry.getKey();
					dictionary = cEntry.getValue();
					log.info("Reusing MorphDictionary instance deserialized from {}", url);
					break;
				}
			}
			if (dictionary == null) {
				cacheKey = new CacheResourceKey(url);
				dictionary = DictionaryDeserializer.from(in, String.valueOf(url));
				log.info("A wordform predictor has not been set in deserialized MorphDictionary");
				instanceCache.put(cacheKey, dictionary);
			}
		}
		// sanity check
		if (cacheKey == null || dictionary == null) {
			throw new IllegalStateException();
		}
		return new GetDictionaryResult(cacheKey, dictionary);
	}

	public GetDictionaryResult getDictionary(File file) throws Exception {
		URL url = file.toURI().toURL();
		InputStream fileIS = FileUtils.openInputStream(file);
		return getDictionary(url, fileIS);
	}
}
