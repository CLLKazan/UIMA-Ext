/**
 * 
 */
package org.nlplab.brat.ann;

import org.nlplab.brat.configuration.BratType;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class BratAnnotation<T extends BratType> extends BratIdentifiable {

	private T type;

	public BratAnnotation(T type) {
		this.type = type;
	}

	public T getType() {
		return type;
	}
}