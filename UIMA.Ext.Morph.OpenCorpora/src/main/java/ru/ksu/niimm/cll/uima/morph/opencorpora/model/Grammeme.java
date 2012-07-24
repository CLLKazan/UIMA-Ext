/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

import java.io.Serializable;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Grammeme implements Serializable {

	private static final long serialVersionUID = 4295735884264399518L;
	private static int idCounter = 1;

	private String id;
	private String parentId;
	private int numId;

	public Grammeme(String id, String parentId) {
		this.id = id;
		this.parentId = parentId;
		this.numId = idCounter++;
	}

	public String getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}

	public int getNumId() {
		return numId;
	}
}