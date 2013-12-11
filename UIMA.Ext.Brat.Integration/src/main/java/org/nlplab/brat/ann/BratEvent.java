/**
 * 
 */
package org.nlplab.brat.ann;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.nlplab.brat.configuration.BratEventType;

import com.google.common.collect.Multimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratEvent extends BratStructureAnnotation<BratEventType> {

	private BratEventTrigger trigger;

	public BratEvent(BratEventType type, BratEventTrigger trigger,
			Multimap<String, ? extends BratAnnotation<?>> roleAnnotations) {
		super(type, roleAnnotations);
		this.trigger = trigger;
	}

	public BratEventTrigger getTrigger() {
		return trigger;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("id", getId()).append("type", getType())
				.append("trigger", trigger).append("roleAnnotation", getRoleAnnotations())
				.toString();
	}
}