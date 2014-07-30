package org.arabidopsis.ahocorasick;

import java.io.Serializable;

/**
 * Simple interface for mapping Strings to States.
 */
interface EdgeList<O extends Serializable> {
	State<O> get(String token);

	void put(String token, State<O> state);

	String[] keys();
}