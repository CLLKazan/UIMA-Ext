package ru.kfu.itis.issst.uima.ml;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.CleartkProcessingException;

import java.io.Closeable;
import java.util.List;

/**
 * @author Rinat Gareev
 */
public interface SequenceDataWriter<I extends AnnotationFS, OUT> extends Closeable {

    /**
     * Write training data for the given sequence.
     * <p>
     * Note! Ensure thread-safety of this operation implementation as it can be invoked simultaneously by several
     * annotators.
     * </p>
     *
     * @param jCas      JCas
     * @param spanAnno  an annotation structure that represents the given sequence, e.g., <b>Sentence</b>
     * @param seq       sequence items
     * @param seqLabels sequence item classification labels
     * @throws CleartkProcessingException
     */
    void write(JCas jCas, Annotation spanAnno, List<? extends I> seq, List<OUT> seqLabels)
            throws CleartkProcessingException;
}
