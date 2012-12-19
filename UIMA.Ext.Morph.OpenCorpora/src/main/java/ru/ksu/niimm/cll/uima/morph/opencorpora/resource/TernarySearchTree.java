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

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Implementation of ternary search tree. A Ternary Search Tree is a data
 * structure that behaves in a manner that is very similar to a HashMap.
 */
public class TernarySearchTree<E> implements Serializable {
	private static final long serialVersionUID = -7594024641367446457L;
	
	private TSTNode rootNode;
	
	public TernarySearchTree() {
	}

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
		getOrCreateNode(key).addData(value);
	}

	/**
	 * Retrieve the object indexed by key.
	 * 
	 * @param key
	 *            A String index.
	 * @return Object The object retrieved from the TernarySearchTree.
	 */
	@SuppressWarnings("unchecked")
	public List<E> get(String key) {
		TSTNode node = getNode(key);
		if (node == null || node.data == null)
			return ImmutableList.of();
		Builder<E> b = ImmutableList.builder();
		for(Object obj : node.data){
			b.add((E) obj);
		}
		return b.build();
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
	protected TSTNode getOrCreateNode(String key)
			throws NullPointerException, IllegalArgumentException {
		if (key == null)
			throw new NullPointerException(
					"attempt to get or create node with null key");
		if (key.length() == 0)
			throw new IllegalArgumentException(
					"attempt to get or create node with key of zero length");
		if (rootNode == null)
			rootNode = new TSTNode(key.charAt(0), null);

		TSTNode currentNode = rootNode;
		int charIndex = 0;
		while (true) {
			int charComp = compareCharsAlphabetically(key.charAt(charIndex),
					currentNode.splitchar);

			if (charComp == 0) {
				charIndex++;
				if (charIndex == key.length())
					return currentNode;
				if (currentNode.relatives[TSTNode.EQKID] == null)
					currentNode.relatives[TSTNode.EQKID] = new TSTNode(
							key.charAt(charIndex), currentNode);
				currentNode = (TSTNode) currentNode.relatives[TSTNode.EQKID];
			} else if (charComp < 0) {
				if (currentNode.relatives[TSTNode.LOKID] == null)
					currentNode.relatives[TSTNode.LOKID] = new TSTNode(
							key.charAt(charIndex), currentNode);
				currentNode = (TSTNode) currentNode.relatives[TSTNode.LOKID];
			} else {
				// charComp must be greater than zero
				if (currentNode.relatives[TSTNode.HIKID] == null)
					currentNode.relatives[TSTNode.HIKID] = new TSTNode(
							key.charAt(charIndex), currentNode);
				currentNode = (TSTNode) currentNode.relatives[TSTNode.HIKID];
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
	public TSTNode getNode(String key) {
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
	protected TSTNode getNode(String key, TSTNode startNode) {
		if (key == null || startNode == null || key.length() == 0)
			return null;
		TSTNode currentNode = startNode;
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
				currentNode = (TSTNode) currentNode.relatives[TSTNode.EQKID];
			} else if (charComp < 0) {
				currentNode = (TSTNode) currentNode.relatives[TSTNode.LOKID];
			} else {
				// charComp must be greater than zero
				currentNode = (TSTNode) currentNode.relatives[TSTNode.HIKID];
			}
		}
	}

	/**
	 * An inner class of TernarySearchTree that represents a node in the tree.
	 */
	private static final class TSTNode implements Serializable {
		private static final long serialVersionUID = -7604776410768461412L;
		
		protected static final int LOKID = 0, EQKID = 1, HIKID = 2; // index
																	// values
																	// for
																	// accessing
																	// relatives
																	// array
		protected char splitchar;

		protected Object[] relatives = new Object[3];
		protected Object[] data;

		protected TSTNode(char splitchar, TSTNode parent) {
			this.splitchar = splitchar;
		}
		
		protected void addData(Object value) {
			if (data == null) {
				data = new Object[1];
			} else {
				Object[] temp = data;
				data = new Object[data.length + 1];
				System.arraycopy(temp, 0, data, 0, temp.length);
			}
			data[data.length - 1] = value;
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
