/**
 * 
 */
package org.nlplab.brat.ann;

import java.util.Map;

import org.nlplab.brat.configuration.BratEventType;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratEvent extends BratStructureAnnotation<BratEventType> {

	private BratEventTrigger trigger;

	public BratEvent(BratEventType type, BratEventTrigger trigger,
			Map<String, ? extends BratAnnotation<?>> roleAnnotations) {
		super(type, roleAnnotations);
		this.trigger = trigger;
	}

	public BratEventTrigger getTrigger() {
		return trigger;
	}
}