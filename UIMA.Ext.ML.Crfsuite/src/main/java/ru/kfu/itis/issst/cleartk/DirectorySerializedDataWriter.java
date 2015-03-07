package ru.kfu.itis.issst.cleartk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.encoder.features.FeaturesEncoder;
import org.cleartk.ml.encoder.outcome.OutcomeEncoder;
import org.cleartk.ml.jar.DirectoryDataWriter;
import org.cleartk.ml.jar.EncodingJarClassifierBuilder;

import java.io.*;

/**
 * @author Rinat Gareev
 */
public abstract class DirectorySerializedDataWriter<CLASSIFIER_BUILDER_TYPE extends EncodingJarClassifierBuilder<? extends CLASSIFIER_TYPE, ENCODED_FEATURES_TYPE, OUTCOME_TYPE, ENCODED_OUTCOME_TYPE>,
        CLASSIFIER_TYPE, ENCODED_FEATURES_TYPE extends Serializable, OUTCOME_TYPE, ENCODED_OUTCOME_TYPE extends Serializable>
        extends DirectoryDataWriter<CLASSIFIER_BUILDER_TYPE, CLASSIFIER_TYPE> {

    public DirectorySerializedDataWriter(File outputDirectory) throws IOException {
        super(outputDirectory);
        this.trainingDataFile = this.classifierBuilder.getTrainingDataFile(this.outputDirectory);
        OutputStream out = new BufferedOutputStream(
                FileUtils.openOutputStream(trainingDataFile));
        this.trainingDataWriter = new ObjectOutputStream(out);
    }

    protected File trainingDataFile;

    protected ObjectOutputStream trainingDataWriter;

    public void setFeaturesEncoder(FeaturesEncoder<ENCODED_FEATURES_TYPE> featuresEncoder) {
        this.classifierBuilder.setFeaturesEncoder(featuresEncoder);
    }

    public void setOutcomeEncoder(OutcomeEncoder<OUTCOME_TYPE, ENCODED_OUTCOME_TYPE> outcomeEncoder) {
        this.classifierBuilder.setOutcomeEncoder(outcomeEncoder);
    }

    @Override
    public void finish() throws CleartkProcessingException {
        IOUtils.closeQuietly(trainingDataWriter);
        super.finish();
    }

}
