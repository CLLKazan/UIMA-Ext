package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.CleartkProcessingException;

import java.io.Closeable;
import java.util.List;

/**
 * @author Rinat Gareev
 */
public interface SequenceDataWriter<OUT> extends Closeable {
    void write(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq, List<OUT> seqLabels)
            throws CleartkProcessingException;
}
