package ru.kfu.itis.issst.cleartk.crfsuite2;

import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.encoder.outcome.StringToStringOutcomeEncoder;
import ru.kfu.itis.issst.cleartk.SequenceSerializedDataWriter_ImplBase;
import ru.kfu.itis.issst.cleartk.SerializableNameNumber;
import ru.kfu.itis.issst.cleartk.StringEncoder;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeClassifierBuilder;
import ru.kfu.itis.issst.cleartk.crfsuite.NameNumberFeaturesEncoder2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

/**
 * @author Rinat Gareev
 */
// We bind to ArrayList as we can't bind to a type that does not implement Serializable
public class CRFSuiteSerializedDataWriter extends SequenceSerializedDataWriter_ImplBase
        <CRFSuiteStringOutcomeClassifierBuilder, ArrayList<SerializableNameNumber>, String, String> {

    public CRFSuiteSerializedDataWriter(File outputDirectory) throws IOException {
        super(outputDirectory);
        NameNumberFeaturesEncoder2 fe = new NameNumberFeaturesEncoder2(null);
        fe.addEncoder(new StringEncoder());
        this.setFeaturesEncoder(fe);
        this.setOutcomeEncoder(new StringToStringOutcomeEncoder());
    }

    @Override
    protected void writeEncoded(ArrayList<SerializableNameNumber> aFeatures, String outcome)
            throws CleartkProcessingException, IOException {
        ArrayList<SerializableNameNumber> features = newArrayListWithExpectedSize(aFeatures.size());
        for (SerializableNameNumber aNN : aFeatures) {
            // a feature value should not refer to its big origin text
            //noinspection RedundantStringConstructorCall
            features.add(new SerializableNameNumber(new String(aNN.name), aNN.number));
        }
        if (outcome != null) {
            outcome = outcome.intern();
        }
        super.writeEncoded(features, outcome);
    }

    @Override
    protected void writeSequenceEnd() throws IOException {
        super.writeSequenceEnd();
        // reset object output stream to avoid OOM
        trainingDataWriter.reset();
    }

    @Override
    protected CRFSuiteStringOutcomeClassifierBuilder newClassifierBuilder() {
        return new CRFSuiteStringOutcomeClassifierBuilder();
    }
}
