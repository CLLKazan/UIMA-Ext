/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CachedResourceTuple<R> {

	private final CacheKey cacheKey;
	private final R resource;

	public CachedResourceTuple(CacheKey cacheKey, R resource) {
		this.cacheKey = cacheKey;
		this.resource = resource;
	}

	public CacheKey getCacheKey() {
		return cacheKey;
	}

	public R getResource() {
		return resource;
	}
}
