/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

import java.io.Serializable;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LemmaLink implements Serializable {

	private static final long serialVersionUID = -4181644229398395488L;

	private int fromLemmaId;
	private int toLemmaId;
	private short linkTypeId;

	public LemmaLink(int fromLemmaId, int toLemmaId, short linkTypeId) {
		this.fromLemmaId = fromLemmaId;
		this.toLemmaId = toLemmaId;
		this.linkTypeId = linkTypeId;
	}

	public int getFromLemmaId() {
		return fromLemmaId;
	}

	public int getToLemmaId() {
		return toLemmaId;
	}

	public short getLinkTypeId() {
		return linkTypeId;
	}
}