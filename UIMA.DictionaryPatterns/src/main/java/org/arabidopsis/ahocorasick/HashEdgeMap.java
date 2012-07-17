/**
 * 
 */
package org.arabidopsis.ahocorasick;

import java.io.Serializable;
import java.util.HashMap;

import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class HashEdgeMap<O extends Serializable> implements EdgeList<O> {

	private HashMap<String, State<O>> map = Maps.newHashMap();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public State<O> get(String token) {
		return map.get(token);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void put(String token, State<O> state) {
		if (map.put(token, state) != null) {
			throw new IllegalStateException("Duplicate out");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] keys() {
		return map.keySet().toArray(new String[map.size()]);
	}

}