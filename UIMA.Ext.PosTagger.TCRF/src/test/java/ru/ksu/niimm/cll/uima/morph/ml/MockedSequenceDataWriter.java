package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.cleartk.classifier.CleartkProcessingException;

import java.io.IOException;
import java.util.List;

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

/**
 * @author Rinat Gareev
 */
public class MockedSequenceDataWriter<OUT> implements SequenceDataWriter<OUT>, SharedResourceObject {
    public static ExternalResourceDescription createDescription() {
        return createExternalResourceDescription(MockedSequenceDataWriter.class, "file:pom.xml");
    }

    private SequenceDataWriter<OUT> mock;

    public void setMock(SequenceDataWriter<OUT> mock) {
        this.mock = mock;
    }

    @Override
    public void load(DataResource aData) throws ResourceInitializationException {
    }

    @Override
    public void write(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq, List<OUT> seqLabels) throws CleartkProcessingException {
        mock.write(jCas, spanAnno, seq, seqLabels);
    }

    @Override
    public void close() throws IOException {
        mock.close();
    }
}
