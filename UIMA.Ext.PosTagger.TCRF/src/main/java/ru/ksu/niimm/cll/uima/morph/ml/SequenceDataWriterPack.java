package ru.ksu.niimm.cll.uima.morph.ml;

import org.cleartk.classifier.SequenceDataWriter;

import java.io.IOException;

/**
 * @author Rinat Gareev
 * @param <OUT> a classifier output type
 */
public interface SequenceDataWriterPack<OUT> {

    SequenceDataWriter<OUT> getDataWriter(int index);

}
