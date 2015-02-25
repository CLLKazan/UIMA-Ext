package ru.ksu.niimm.cll.uima.morph.ml;

import org.cleartk.classifier.SequenceClassifier;
import ru.kfu.itis.cll.uima.util.ResourceTicket;

import java.io.Closeable;

/**
 * Note! Do not forget to {@link #acquire()} a ticket before starting to use this resource
 * and {@link ru.kfu.itis.cll.uima.util.ResourceTicket#close()} it after.
 *
 * @param <OUT> a classifier output type
 * @author Rinat Gareev
 */
interface SeqClassifierPack<OUT> {

    SequenceClassifier<OUT> getClassifier(int index);

    /**
     * Notify this pack about a new client.
     */
    ResourceTicket acquire();
}
