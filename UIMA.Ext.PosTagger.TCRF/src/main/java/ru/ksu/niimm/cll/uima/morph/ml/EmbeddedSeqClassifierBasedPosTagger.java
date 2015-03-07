package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.ml.SequenceClassifier;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

import java.io.File;

import static java.lang.String.format;

/**
 * A PoS-tagger annotator where each annotator copy has its own instance of {@link ru.kfu.itis.issst.uima.ml.SequenceClassifier}.
 *
 * @author Rinat Gareev
 */
public class EmbeddedSeqClassifierBasedPosTagger extends SeqClassifierBasedPosTaggerBase {

    public static AnalysisEngineDescription createDescription(File modelBaseDir)
            throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(EmbeddedSeqClassifierBasedPosTagger.class,
                PosTaggerAPI.getTypeSystemDescription(),
                PARAM_MODEL_BASE_DIR, modelBaseDir.getPath());
    }

    public static AnalysisEngineDescription createDescription(String modelBasePath)
            throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(EmbeddedSeqClassifierBasedPosTagger.class,
                PosTaggerAPI.getTypeSystemDescription(),
                PARAM_MODEL_BASE_PATH, modelBasePath);
    }

    //
    public static final String PARAM_MODEL_BASE_DIR = "modelBaseDir";
    public static final String PARAM_MODEL_BASE_PATH = "modelBasePath";
    // config
    @ConfigurationParameter(name = PARAM_MODEL_BASE_DIR, mandatory = false)
    private File modelBaseDir;
    @ConfigurationParameter(name = PARAM_MODEL_BASE_PATH, mandatory = false)
    private String modelBasePath;
    // aggregates
    private SequenceClassifier<Token, String[]> classifier;

    @Override
    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        //
        if (modelBaseDir == null) {
            if (modelBasePath == null) {
                throw new IllegalStateException("Both modelBasePath & modelBaseDir are not specified");
            }
            try {
                modelBaseDir = TieredSequenceClassifiers.resolveModelBaseDir(modelBasePath, null);
            } catch (Exception e) {
                throw new ResourceInitializationException(e);
            }
        }
        if (!modelBaseDir.isDirectory()) {
            throw new IllegalStateException(format(
                    "%s is not a directory", modelBaseDir));
        }
        //
        //noinspection unchecked
        classifier = (SequenceClassifier<Token, String[]>) TieredSequenceClassifiers.fromModelBaseDir(modelBaseDir);
    }

    @Override
    protected SequenceClassifier<Token, String[]> getClassifier() {
        return classifier;
    }

    @Override
    public void destroy() {
        IOUtils.closeQuietly(classifier);
        super.destroy();
    }
}
