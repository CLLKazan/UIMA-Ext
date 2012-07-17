package org.arabidopsis.ahocorasick;

import java.io.Serializable;
import java.util.Set;

/**
 * <p>
 * Holds the result of the search so far. Includes the outputs where the search
 * finished as well as the last index of the matching.
 * </p>
 * 
 * <p>
 * (Internally, it also holds enough state to continue a running search, though
 * this is not exposed for public use.)
 * </p>
 */
public class SearchResult<O extends Serializable> {
	State<O> lastMatchedState;
	String[] tokens;
	int lastIndex;

	SearchResult(State<O> s, String[] tokens, int i) {
		this.lastMatchedState = s;
		this.tokens = tokens;
		this.lastIndex = i;
	}

	/**
	 * Returns a list of the outputs of this match.
	 */
	public Set<O> getOutputs() {
		return lastMatchedState.getOutputs();
	}

	/**
	 * Returns the index where the search terminates. Note that this is one
	 * after the last matching token.
	 */
	public int getLastIndex() {
		return lastIndex;
	}
}
