package ru.kfu.itis.issst.cleartk;

import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.Instance;
import org.cleartk.ml.SequenceClassifier;
import org.cleartk.ml.SequenceDataWriter;
import org.cleartk.ml.jar.EncodingJarClassifierBuilder;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * @author Rinat Gareev
 */
public abstract class SequenceSerializedDataWriter_ImplBase<CLASSIFIER_BUILDER_TYPE extends EncodingJarClassifierBuilder<? extends SequenceClassifier<OUTCOME_TYPE>, ENCODED_FEATURES_TYPE, OUTCOME_TYPE, ENCODED_OUTCOME_TYPE>, ENCODED_FEATURES_TYPE extends Serializable, OUTCOME_TYPE, ENCODED_OUTCOME_TYPE extends Serializable>
        extends DirectorySerializedDataWriter<CLASSIFIER_BUILDER_TYPE, SequenceClassifier<OUTCOME_TYPE>, ENCODED_FEATURES_TYPE, OUTCOME_TYPE, ENCODED_OUTCOME_TYPE>
        implements SequenceDataWriter<OUTCOME_TYPE> {

    public SequenceSerializedDataWriter_ImplBase(File outputDirectory) throws IOException {
        super(outputDirectory);
    }

    @Override
    public void write(List<Instance<OUTCOME_TYPE>> instances) throws CleartkProcessingException {
        try {
            for (Instance<OUTCOME_TYPE> instance : instances) {
                writeEncoded(
                        this.classifierBuilder.getFeaturesEncoder().encodeAll(instance.getFeatures()),
                        this.classifierBuilder.getOutcomeEncoder().encode(instance.getOutcome()));
            }
            writeSequenceEnd();
        } catch (IOException e) {
            throw new CleartkProcessingException(e);
        }
    }

    protected void writeEncoded(ENCODED_FEATURES_TYPE features, ENCODED_OUTCOME_TYPE outcome)
            throws CleartkProcessingException, IOException {
        trainingDataWriter.writeObject(features);
        trainingDataWriter.writeObject(outcome);
    }

    protected void writeSequenceEnd() throws IOException {
        trainingDataWriter.writeObject(SENTENCE_END);
    }

    @Override
    public void finish() throws CleartkProcessingException {
        try {
            trainingDataWriter.writeObject(DATA_END);
        } catch (IOException e) {
            throw new CleartkProcessingException(e);
        }
        super.finish();
    }

    private static final SentenceEnd SENTENCE_END = new SentenceEnd();

    private static class SentenceEnd implements Serializable {
        private SentenceEnd() {
        }
    }

    private static final DataEnd DATA_END = new DataEnd();

    private static class DataEnd implements Serializable {
        private DataEnd() {
        }
    }

    public static boolean isSequenceEnd(Object o) {
        return o instanceof SentenceEnd;
    }

    public static boolean isDataEnd(Object o) {
        return o instanceof DataEnd;
    }
}
