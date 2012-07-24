/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

import java.io.Serializable;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LemmaLinkType implements Serializable {

	private static final long serialVersionUID = 5035555881382048689L;

	private short id;
	private String name;

	public LemmaLinkType(short id, String name) {
		this.id = id;
		this.name = name;
	}

	public short getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}