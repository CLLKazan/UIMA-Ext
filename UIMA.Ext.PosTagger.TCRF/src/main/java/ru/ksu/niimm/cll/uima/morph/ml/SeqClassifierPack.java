package ru.ksu.niimm.cll.uima.morph.ml;

import org.cleartk.classifier.SequenceClassifier;

import java.io.Closeable;

/**
 * @author Rinat Gareev
 * @param <OUT> a classifier output type
 */
public interface SeqClassifierPack<OUT> extends Closeable {

    SequenceClassifier<OUT> getClassifier(int index);

    @Override
    void close();
}
