package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.List;

/**
 * @author Rinat Gareev
 * @param <OUT> outcome type for each element of a sequence
 */
public interface SequenceClassifier<OUT> {
    List<OUT> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq);
}
