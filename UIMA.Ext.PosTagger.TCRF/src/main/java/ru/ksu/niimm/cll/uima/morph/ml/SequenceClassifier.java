package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.io.Closeable;
import java.util.List;

/**
 * @param <OUT> outcome type for each element of a sequence
 * @author Rinat Gareev
 */
public interface SequenceClassifier<OUT> extends Closeable {
    List<OUT> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq);
}
