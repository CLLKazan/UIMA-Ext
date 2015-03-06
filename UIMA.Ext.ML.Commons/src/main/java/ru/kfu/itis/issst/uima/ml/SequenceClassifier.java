package ru.kfu.itis.issst.uima.ml;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.CleartkProcessingException;

import java.io.Closeable;
import java.util.List;

/**
 * @param  <I> a sequence element type
 * @param <OUT> an outcome type for each element of a sequence
 * @author Rinat Gareev
 */
public interface SequenceClassifier<I extends AnnotationFS, OUT> extends Closeable {
    List<OUT> classify(JCas jCas, Annotation spanAnno, List<? extends I> seq)
            throws CleartkProcessingException;
}
