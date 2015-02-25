package ru.ksu.niimm.cll.uima.morph.ml;

import org.cleartk.classifier.SequenceDataWriter;
import ru.kfu.itis.cll.uima.util.ResourceTicket;

import java.io.IOException;

/**
 * Note! Do not forget to {@link #acquire()} a ticket before starting to use this resource
 * and {@link ru.kfu.itis.cll.uima.util.ResourceTicket#close()} it after.
 *
 * @param <OUT> a classifier output type
 * @author Rinat Gareev
 */
interface SequenceDataWriterPack<OUT> {

    SequenceDataWriter<OUT> getDataWriter(int index);

    /**
     * Notify this pack about a new client.
     */
    ResourceTicket acquire();
}
