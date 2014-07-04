package ru.kfu.itis.issst.uima.morph.compare;

public interface AnnotationDao {
	EnclosedAnnotationEntity getAnnotation(String docUri, int begin, int end);

	EnclosedAnnotationEntity getAnnotation(long id);

	EnclosedAnnotationEntity save(EnclosedAnnotationEntity annoEntity);
}
