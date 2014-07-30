/**
 * 
 */
package org.arabidopsis.ahocorasick;

import java.io.Serializable;

/**
 * Modified to return itself for unspecified input after preparation is
 * complete.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class RootState<O extends Serializable> extends State<O> {

	private boolean prepared = false;

	public RootState() {
		super(0);
	}

	@Override
	public State<O> get(String token) {
		State<O> result = super.get(token);
		if (!prepared) {
			return result;
		} else if (result == null) {
			return this;
		}
		return result;
	}

	boolean isPrepared() {
		return prepared;
	}

	public void setPrepared(boolean prepared) {
		this.prepared = prepared;
	}
}