package ru.kfu.itis.issst.cleartk.crfsuite2;

import org.cleartk.ml.SequenceDataWriter;
import org.cleartk.ml.SequenceDataWriterFactory;
import org.cleartk.ml.jar.DirectoryDataWriterFactory;

import java.io.IOException;

/**
 * @author Rinat Gareev
 */
public class CRFSuiteSerializedDataWriterFactory extends DirectoryDataWriterFactory
        implements SequenceDataWriterFactory<String> {

    @Override
    public SequenceDataWriter<String> createDataWriter() throws IOException {
        return new CRFSuiteSerializedDataWriter(outputDirectory);
    }
}
