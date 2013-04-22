/**
 * 
 */
package org.nlplab.brat.ann;

import org.nlplab.brat.configuration.BratEntityType;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratEntity extends BratTextBoundAnnotation<BratEntityType> {

	public BratEntity(BratEntityType type, int begin, int end, String spannedText) {
		super(type, begin, end, spannedText);
	}
}