/**
 * 
 */
package org.nlplab.brat.configuration;

/**
 * @author Rinat Gareev
 * 
 */
public abstract class BratType {

	protected final String name;

	protected BratType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}