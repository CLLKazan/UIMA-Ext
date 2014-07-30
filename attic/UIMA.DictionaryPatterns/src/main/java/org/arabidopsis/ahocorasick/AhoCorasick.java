package org.arabidopsis.ahocorasick;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Queue;

import com.google.common.collect.Lists;

/**
 * Modified implementation of
 * https://hkn.eecs.berkeley.edu/~dyoo/java/index.html to work with String
 * objects instead of bytes.
 */
public class AhoCorasick<O extends Serializable> {
	private RootState<O> root;
	private boolean prepared;

	public AhoCorasick() {
		this.root = new RootState<O>();
		this.prepared = false;
	}

	/**
	 * Adds a new keyword with the given output. During search, if the keyword
	 * is matched, output will be one of the yielded elements in
	 * SearchResults.getOutputs().
	 */
	public void add(String[] keyword, O output) {
		if (this.prepared)
			throw new IllegalStateException("can't add keywords after prepare() is called");
		State<O> lastState = this.root.extendAll(keyword);
		lastState.addOutput(output);
	}

	/**
	 * Prepares the automaton for searching. This must be called before any
	 * searching().
	 */
	public void prepare() {
		this.prepareFailTransitions();
		this.prepared = true;
		assert root.isPrepared();
	}

	/**
	 * Starts a new search, and returns an Iterator of SearchResults.
	 */
	public Iterator<SearchResult<O>> search(String[] input) {
		return new Searcher<O>(this, this.startSearch(input));
	}

	/**
	 * DANGER DANGER: dense algorithm code ahead. Very order dependent.
	 * Initializes the fail transitions of all states except for the root.
	 */
	private void prepareFailTransitions() {
		Queue<State<O>> q = Lists.newLinkedList();
		for (String rootOut : root.keys()) {
			State<O> state = root.get(rootOut);
			state.setFail(root);
			q.add(state);
		}
		this.prepareRoot();
		while (!q.isEmpty()) {
			State<O> state = q.remove();
			String[] keys = state.keys();
			for (String a : keys) {
				State<O> r = state;
				State<O> s = r.get(a);
				q.add(s);
				r = r.getFail();
				while (r.get(a) == null)
					r = r.getFail();
				s.setFail(r.get(a));
				s.getOutputs().addAll(r.get(a).getOutputs());
			}
		}
	}

	/**
	 * Sets all the out transitions of the root to itself, if no transition yet
	 * exists at this point.
	 */
	private void prepareRoot() {
		root.setPrepared(true);
	}

	/**
	 * Returns the root of the tree. Package protected, since the user probably
	 * shouldn't touch this.
	 */
	State<O> getRoot() {
		return this.root;
	}

	/**
	 * Begins a new search using the raw interface. Package protected.
	 */
	SearchResult<O> startSearch(String[] input) {
		if (!this.prepared)
			throw new IllegalStateException("can't start search until prepare()");
		return continueSearch(new SearchResult<O>(this.root, input, 0));
	}

	/**
	 * Continues the search, given the initial state described by the
	 * lastResult. Package protected.
	 */
	SearchResult<O> continueSearch(SearchResult<O> lastResult) {
		String[] tokens = lastResult.tokens;
		State<O> state = lastResult.lastMatchedState;
		for (int i = lastResult.lastIndex; i < tokens.length; i++) {
			String curToken = tokens[i];
			while (state.get(curToken) == null)
				state = state.getFail();
			state = state.get(curToken);
			if (state.getOutputs().size() > 0)
				return new SearchResult<O>(state, tokens, i + 1);
		}
		return null;
	}

}