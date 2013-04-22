/**
 * 
 */
package org.nlplab.brat.ann;

import java.util.Map;

import org.nlplab.brat.configuration.BratEventType;

import com.google.common.collect.ImmutableMap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratEvent extends BratAnnotation<BratEventType> {

	private BratEventTrigger trigger;
	private Map<String, BratAnnotation<?>> roleAnnotations;

	public BratEvent(BratEventType type, BratEventTrigger trigger,
			Map<String, BratAnnotation<?>> roleAnnotations) {
		super(type);
		this.trigger = trigger;
		this.roleAnnotations = ImmutableMap.copyOf(roleAnnotations);
	}
}