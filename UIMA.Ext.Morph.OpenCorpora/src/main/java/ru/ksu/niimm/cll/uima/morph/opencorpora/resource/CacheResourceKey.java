/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.net.URL;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class CacheResourceKey {
	private URL url;

	public CacheResourceKey(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}
}