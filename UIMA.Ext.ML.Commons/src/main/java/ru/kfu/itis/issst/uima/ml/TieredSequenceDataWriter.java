package ru.kfu.itis.issst.uima.ml;

import org.apache.uima.cas.text.AnnotationFS;

import java.util.List;

/**
 * @param <I>    an item type of input sequence (e.g., token)
 * @param <TOUT> a single tier label type
 * @author Rinat Gareev
 */
public interface TieredSequenceDataWriter<I extends AnnotationFS, TOUT> extends SequenceDataWriter<I, TOUT[]> {
    List<String> getTierIds();
}
