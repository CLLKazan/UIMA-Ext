package ru.kfu.itis.issst.uima.morph.compare;

public class AnnotationEntity extends FSEntity {
	private Integer begin;
	private Integer end;
	private String coveredText;

	public AnnotationEntity(Long id, String docUri, Integer begin, Integer end, String coveredText) {
		super(id, docUri);
		this.begin = begin;
		this.end = end;
		this.coveredText = coveredText;
	}

	public Integer getBegin() {
		return begin;
	}

	public Integer getEnd() {
		return end;
	}

	public String getCoveredText() {
		return coveredText;
	}
}
