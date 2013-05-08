/**
 * 
 */
package org.nlplab.brat.ann;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.nlplab.brat.configuration.BratType;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class BratTextBoundAnnotation<T extends BratType> extends BratAnnotation<T> {

	private int begin;
	private int end;
	private String spannedText;

	public BratTextBoundAnnotation(T type, int begin, int end, String spannedText) {
		super(type);
		this.begin = begin;
		this.end = end;
		this.spannedText = spannedText;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getSpannedText() {
		return spannedText;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("id", getId()).append("type", getType())
				.append("begin", getBegin()).append("end", getEnd())
				.append("spannedText", getSpannedText()).toString();
	}
}