/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.net.URL;

import ru.kfu.itis.cll.uima.util.CacheKey;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class CacheResourceKey implements CacheKey {
	private URL url;

	public CacheResourceKey(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}
}