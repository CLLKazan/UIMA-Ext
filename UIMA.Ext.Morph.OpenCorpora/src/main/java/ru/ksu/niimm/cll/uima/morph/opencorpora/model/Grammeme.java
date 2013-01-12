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
	private String alias;
	private String description;
	private int numId;

	public Grammeme(String id, String parentId, String alias, String description) {
		this.id = id;
		this.parentId = parentId;
		this.numId = idCounter++;

		this.alias = alias;
		this.description = description;
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

	public String getAlias() {
		return alias;
	}

	public String getDescription() {
		return description;
	}
}