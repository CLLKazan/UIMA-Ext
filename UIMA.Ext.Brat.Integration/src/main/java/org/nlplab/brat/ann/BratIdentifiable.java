/**
 * 
 */
package org.nlplab.brat.ann;

/**
 * @author Rinat Gareev
 *
 */
public abstract class BratIdentifiable {

	private String id;
	private Long numId;

	public String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

	protected void setNumId(Long id) {
		this.numId = id;
	}

	protected long getNumId() {
		return numId;
	}

}