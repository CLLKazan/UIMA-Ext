package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;

import java.util.List;

/**
 * @author Rinat Gareev
 */
public interface IncrementalFeatureExtractor {

    List<Feature> extractNext(JCas view, Annotation contextSpan, Annotation focusAnnotation);

}
