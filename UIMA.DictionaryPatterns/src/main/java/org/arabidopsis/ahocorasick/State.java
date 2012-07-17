package org.arabidopsis.ahocorasick;

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A state represents an element in the Aho-Corasick tree.
 */

class State<O extends Serializable> {

	private int depth;
	private EdgeList<O> edgeList;
	private State<O> fail;
	private Set<O> outputs;

	public State(int depth) {
		this.depth = depth;
		this.edgeList = new HashEdgeMap<O>();
		this.fail = null;
		this.outputs = Sets.newHashSet();
	}

	public State<O> extend(String token) {
		State<O> nextState = this.edgeList.get(token);
		if (nextState != null)
			return nextState;
		nextState = new State<O>(this.depth + 1);
		this.edgeList.put(token, nextState);
		return nextState;
	}

	public State<O> extendAll(String[] tokens) {
		State<O> state = this;
		for (int i = 0; i < tokens.length; i++) {
			State<O> tmpState = state.edgeList.get(tokens[i]);
			if (tmpState == null) {
				tmpState = state.extend(tokens[i]);
			}
			state = tmpState;
		}
		return state;
	}

	/**
	 * Returns the size of the tree rooted at this State. Note: do not call this
	 * if there are loops in the edgelist graph, such as those introduced by
	 * AhoCorasick.prepare().
	 */
	public int size() {
		String[] keys = edgeList.keys();
		int result = 1;
		for (int i = 0; i < keys.length; i++)
			result += edgeList.get(keys[i]).size();
		return result;
	}

	public State<O> get(String token) {
		return this.edgeList.get(token);
	}

	public void put(String t, State<O> s) {
		this.edgeList.put(t, s);
	}

	public String[] keys() {
		return this.edgeList.keys();
	}

	public State<O> getFail() {
		return this.fail;
	}

	public void setFail(State<O> f) {
		this.fail = f;
	}

	public void addOutput(O o) {
		this.outputs.add(o);
	}

	public Set<O> getOutputs() {
		return this.outputs;
	}
}
