/**
 * 
 */
package org.nlplab.brat.ann;

import org.nlplab.brat.configuration.BratEventType;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratEventTrigger extends BratTextBoundAnnotation<BratEventType> {

	public BratEventTrigger(BratEventType type, int begin, int end, String spannedText) {
		super(type, begin, end, spannedText);
	}
}