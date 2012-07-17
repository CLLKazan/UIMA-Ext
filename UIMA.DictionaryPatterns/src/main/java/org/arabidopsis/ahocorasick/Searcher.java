package org.arabidopsis.ahocorasick;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator returns a list of Search matches.
 */

class Searcher<O extends Serializable> implements Iterator<SearchResult<O>> {
	private SearchResult<O> currentResult;
	private AhoCorasick<O> tree;

	Searcher(AhoCorasick<O> tree, SearchResult<O> result) {
		this.tree = tree;
		this.currentResult = result;
	}

	public boolean hasNext() {
		return (this.currentResult != null);
	}

	public SearchResult<O> next() {
		if (!hasNext())
			throw new NoSuchElementException();
		SearchResult<O> result = currentResult;
		currentResult = tree.continueSearch(currentResult);
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
