package ru.kfu.itis.issst.uima.morph.compare;

public class EnclosedAnnotationEntity extends AnnotationEntity {
	private Long enclosingAnnotationId;

	public EnclosedAnnotationEntity(Long id,
			String docUri, Integer begin, Integer end, String coveredText,
			Long enclosingAnnotationId) {
		super(id, docUri, begin, end, coveredText);
		this.enclosingAnnotationId = enclosingAnnotationId;
	}

	public EnclosedAnnotationEntity(Long id, EnclosedAnnotationEntity src) {
		super(id, src.getDocUri(), src.getBegin(), src.getEnd(), src.getCoveredText());
		this.enclosingAnnotationId = src.getEnclosingAnnotationId();
	}

	public Long getEnclosingAnnotationId() {
		return enclosingAnnotationId;
	}
}
