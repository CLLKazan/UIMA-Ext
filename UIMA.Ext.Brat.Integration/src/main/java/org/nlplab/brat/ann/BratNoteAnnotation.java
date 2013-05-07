/**
 * 
 */
package org.nlplab.brat.ann;

import org.nlplab.brat.configuration.BratNoteType;

import com.google.common.base.Objects;

/**
 * @author Rinat Gareev
 * 
 */
public class BratNoteAnnotation extends BratAnnotation<BratNoteType> {

	private BratAnnotation<?> targetAnnotation;
	private String content;

	public BratNoteAnnotation(BratNoteType type, BratAnnotation<?> targetAnnotation, String content) {
		super(type);
		this.targetAnnotation = targetAnnotation;
		this.content = content;
	}

	public BratAnnotation<?> getTargetAnnotation() {
		return targetAnnotation;
	}

	public String getContent() {
		return content;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BratNoteAnnotation)) {
			return false;
		}
		BratNoteAnnotation that = (BratNoteAnnotation) obj;
		return Objects.equal(this.getId(), that.getId());
	}
}