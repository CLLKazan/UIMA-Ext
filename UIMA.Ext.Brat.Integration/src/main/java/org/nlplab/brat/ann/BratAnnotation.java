/**
 * 
 */
package org.nlplab.brat.ann;

import org.nlplab.brat.configuration.BratType;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class BratAnnotation<T extends BratType> {

	private T type;
	private String id;
	// numeric id should be used only for serialization purposes within package
	private Long numId;

	public BratAnnotation(T type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	void setNumId(Long id) {
		this.numId = id;
	}

	long getNumId() {
		return numId;
	}

	public T getType() {
		return type;
	}
}