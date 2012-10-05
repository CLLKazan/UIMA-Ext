/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2008 Cheok YanCheng <yccheok@yahoo.com>
 * 
 * Modified by Yan Cheng for generic introduction and thread safe matchPrefix.
 * Original Author Wally Flint: wally@wallyflint.com
 * With thanks to Michael Amster of webeasy.com for introducing Wally Flint to 
 * the Ternary Search Tree, and providing some starting code.
 */

package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.*;

/**
 * Implementation of ternary search tree. A Ternary Search Tree is a data
 * structure that behaves in a manner that is very similar to a HashMap.
 */
public class TernarySearchTree<E> {
	private TSTNode<E> rootNode;

	/**
	 * Stores value in the TernarySearchTree. The value may be retrieved using
	 * key.
	 * 
	 * @param key
	 *            A string that indexes the object to be stored.
	 * @param value
	 *            The object to be stored in the tree.
	 */
	public void put(String key, E value) {
		getOrCreateNode(key).data.add(value);
	}

	/**
	 * Retrieve the object indexed by key.
	 * 
	 * @param key
	 *            A String index.
	 * @return Object The object retrieved from the TernarySearchTree.
	 */
	public List<E> get(String key) {
		TSTNode<E> node = getNode(key);
		if (node == null)
			return null;
		return node.data;
	}

	/**
	 * Returns the Node indexed by key, creating that node if it doesn't exist,
	 * and creating any required. intermediate nodes if they don't exist.
	 * 
	 * @param key
	 *            A string that indexes the node that is returned.
	 * @return TSTNode The node object indexed by key. This object is an
	 *         instance of an inner class named TernarySearchTree.TSTNode.
	 */
	protected TSTNode<E> getOrCreateNode(String key)
			throws NullPointerException, IllegalArgumentException {
		if (key == null)
			throw new NullPointerException(
					"attempt to get or create node with null key");
		if (key.length() == 0)
			throw new IllegalArgumentException(
					"attempt to get or create node with key of zero length");
		if (rootNode == null)
			rootNode = new TSTNode<E>(key.charAt(0), null);

		TSTNode<E> currentNode = rootNode;
		int charIndex = 0;
		while (true) {
			int charComp = compareCharsAlphabetically(key.charAt(charIndex),
					currentNode.splitchar);

			if (charComp == 0) {
				charIndex++;
				if (charIndex == key.length())
					return currentNode;
				if (currentNode.relatives[TSTNode.EQKID] == null)
					currentNode.relatives[TSTNode.EQKID] = new TSTNode<E>(
							key.charAt(charIndex), currentNode);
				currentNode = currentNode.relatives[TSTNode.EQKID];
			} else if (charComp < 0) {
				if (currentNode.relatives[TSTNode.LOKID] == null)
					currentNode.relatives[TSTNode.LOKID] = new TSTNode<E>(
							key.charAt(charIndex), currentNode);
				currentNode = currentNode.relatives[TSTNode.LOKID];
			} else {
				// charComp must be greater than zero
				if (currentNode.relatives[TSTNode.HIKID] == null)
					currentNode.relatives[TSTNode.HIKID] = new TSTNode<E>(
							key.charAt(charIndex), currentNode);
				currentNode = currentNode.relatives[TSTNode.HIKID];
			}
		}
	}

	/**
	 * Returns the Node indexed by key, or null if that node doesn't exist.
	 * Search begins at root node.
	 * 
	 * @param key
	 *            An index that points to the desired node.
	 * @return TSTNode The node object indexed by key. This object is an
	 *         instance of an inner class named TernarySearchTree.TSTNode.
	 */
	public TSTNode<E> getNode(String key) {
		return getNode(key, rootNode);
	}

	/**
	 * Returns the Node indexed by key, or null if that node doesn't exist.
	 * Search begins at root node.
	 * 
	 * @param key
	 *            An index that points to the desired node.
	 * @param startNode
	 *            The top node defining the subtree to be searched.
	 * @return TSTNode The node object indexed by key. This object is an
	 *         instance of an inner class named TernarySearchTree.TSTNode.
	 */
	protected TSTNode<E> getNode(String key, TSTNode<E> startNode) {
		if (key == null || startNode == null || key.length() == 0)
			return null;
		TSTNode<E> currentNode = startNode;
		int charIndex = 0;

		while (true) {
			if (currentNode == null)
				return null;
			int charComp = compareCharsAlphabetically(key.charAt(charIndex),
					currentNode.splitchar);

			if (charComp == 0) {
				charIndex++;
				if (charIndex == key.length())
					return currentNode;
				currentNode = currentNode.relatives[TSTNode.EQKID];
			} else if (charComp < 0) {
				currentNode = currentNode.relatives[TSTNode.LOKID];
			} else {
				// charComp must be greater than zero
				currentNode = (TSTNode<E>) currentNode.relatives[TSTNode.HIKID];
			}
		}
	}

	/**
	 * An inner class of TernarySearchTree that represents a node in the tree.
	 */
	private static final class TSTNode<E> {
		protected static final int PARENT = 0, LOKID = 1, EQKID = 2, HIKID = 3; // index
																				// values
																				// for
																				// accessing
																				// relatives
																				// array
		protected char splitchar;

		@SuppressWarnings("unchecked")
		protected TSTNode<E>[] relatives = new TSTNode[4];
		protected ArrayList<E> data = new ArrayList<E>();

		protected TSTNode(char splitchar, TSTNode<E> parent) {
			this.splitchar = splitchar;
			relatives[PARENT] = parent;
		}
	}

	private static int compareCharsAlphabetically(char cCompare, char cRef) {
		return (alphabetizeChar(cCompare) - alphabetizeChar(cRef));
	}

	private static int alphabetizeChar(char c) {
		if (c < 65)
			return c;
		if (c < 89)
			return (2 * c) - 65;
		if (c < 97)
			return c + 24;
		if (c < 121)
			return (2 * c) - 128;

		return c;
	}
}
