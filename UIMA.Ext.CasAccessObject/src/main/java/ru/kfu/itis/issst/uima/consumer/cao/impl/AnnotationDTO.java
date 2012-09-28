/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao.impl;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AnnotationDTO {

	private long id = -1;
	private String txt;
	private String type;

	// doc-related
	private String docUri;
	private int startOffset;
	private int endOffset;

	public long getId() {
		return id;
	}

	void setId(long id) {
		this.id = id;
	}

	public String getTxt() {
		return txt;
	}

	void setTxt(String txt) {
		this.txt = txt;
	}

	public String getType() {
		return type;
	}

	void setType(String type) {
		this.type = type;
	}

	public String getDocUri() {
		return docUri;
	}

	void setDocUri(String docUri) {
		this.docUri = docUri;
	}

	public int getStartOffset() {
		return startOffset;
	}

	void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", id)
				.append("type", type)
				.append("txt", txt)
				.append("docUri", docUri)
				.append("startOffset", startOffset)
				.append("endOffset", endOffset).toString();
	}
}