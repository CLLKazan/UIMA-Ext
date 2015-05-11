package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.cleartk.ml.CleartkProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import ru.kfu.itis.issst.uima.ml.SequenceClassifier;

import java.io.IOException;
import java.util.List;

/**
 * @author Rinat Gareev
 */
public class SeqClassifierResourceBasedPosTaggerLifecycleTest {

    @Mock
    private SequenceClassifier<Annotation, String> classifierMock;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        delegate = classifierMock;
    }

    @Test
    public void initAndDestroy() throws ResourceInitializationException {
        AnalysisEngineDescription taggerDesc = SeqClassifierResourceBasedPosTagger.createDescription();
        // bind classifier resource
        ExternalResourceDescription classifierDesc = ExternalResourceFactory.createExternalResourceDescription(
                StaticSequenceClassifierWrapper.class, "file:pom.xml");
        ExternalResourceFactory.bindExternalResource(
                taggerDesc, SeqClassifierResourceBasedPosTagger.RESOURCE_CLASSIFIER, classifierDesc);
        // stub

        // invoke
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(taggerDesc);
        ae.destroy();
        // verify

    }


    static SequenceClassifier<Annotation, String> delegate;

    public static class StaticSequenceClassifierWrapper implements SequenceClassifier<Annotation, String>, SharedResourceObject {

        @Override
        public List<String> classify(JCas jCas, Annotation spanAnno, List<? extends Annotation> seq)
                throws CleartkProcessingException {
            return delegate.classify(jCas, spanAnno, seq);
        }

        @Override
        public void close() throws IOException {

        }

        @Override
        public void load(DataResource data) throws ResourceInitializationException {
        }

        @Override
        public void onCASChange(JCas cas) {
            throw new UnsupportedOperationException();
        }
    }
}
