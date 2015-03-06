/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.ml.SequenceClassifier;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

/**
 * A PoS-tagger annotator where annotator copies share one instance of {@link ru.kfu.itis.issst.uima.ml.SequenceClassifier}
 * (as UIMA external resource).
 *
 * @author Rinat Gareev (Kazan Federal University)
 */
public class SeqClassifierResourceBasedPosTagger extends SeqClassifierBasedPosTaggerBase {

    public static AnalysisEngineDescription createDescription() throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(
                SeqClassifierResourceBasedPosTagger.class,
                PosTaggerAPI.getTypeSystemDescription());
    }

    public static AnalysisEngineDescription createDescription(
            ExternalResourceDescription classifierResourceDesc)
            throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(
                SeqClassifierResourceBasedPosTagger.class,
                PosTaggerAPI.getTypeSystemDescription(),
                RESOURCE_CLASSIFIER, classifierResourceDesc);
    }

    public static final String RESOURCE_CLASSIFIER = "classifier";

    // config fields
    @ExternalResource(key = RESOURCE_CLASSIFIER, mandatory = true)
    private SequenceClassifier<Token, String[]> classifier;

    @Override
    protected SequenceClassifier<Token, String[]> getClassifier() {
        return classifier;
    }
}
