package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ExternalResourceFactory;
import ru.kfu.itis.cll.uima.util.ResourceTicket;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Rinat Gareev
 */
public class SequenceClassifierBasedPosTaggerLifecycleTest {

    @Mock
    private SequenceClassifierResource<String> classifierMock;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        delegate = classifierMock;
    }

    @Test
    public void initAndDestroy() throws ResourceInitializationException {
        AnalysisEngineDescription taggerDesc = SeqClassifierBasedPosTagger.createDescription();
        // bind classifier resource
        ExternalResourceDescription classifierDesc = ExternalResourceFactory.createExternalResourceDescription(
                StaticSequenceClassifierWrapper.class, "file:pom.xml");
        ExternalResourceFactory.bindExternalResource(
                taggerDesc, SeqClassifierBasedPosTagger.RESOURCE_CLASSIFIER, classifierDesc);
        // stub
        ResourceTicket ticketMock = mock(ResourceTicket.class);
        when(classifierMock.acquire()).thenReturn(ticketMock);
        // invoke
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(taggerDesc);
        ae.destroy();
        // verify
        verify(ticketMock).close();
    }


    static SequenceClassifierResource<String> delegate;

    public static class StaticSequenceClassifierWrapper implements SequenceClassifierResource<String>, SharedResourceObject {

        @Override
        public ResourceTicket acquire() {
            return delegate.acquire();
        }

        @Override
        public List<String> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq) {
            return delegate.classify(jCas, spanAnno, seq);
        }

        @Override
        public void load(DataResource data) throws ResourceInitializationException {
        }
    }
}
