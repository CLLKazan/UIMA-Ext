package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.cleartk.ml.CleartkProcessingException;
import ru.kfu.itis.issst.uima.ml.TieredSequenceDataWriter;

import java.io.IOException;
import java.util.List;

import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

/**
 * @author Rinat Gareev
 */
public class MockedSequenceDataWriter<I extends AnnotationFS, OUT>
        implements TieredSequenceDataWriter<I, OUT>, SharedResourceObject {
    public static ExternalResourceDescription createDescription() {
        return createExternalResourceDescription(MockedSequenceDataWriter.class, "file:pom.xml");
    }

    private TieredSequenceDataWriter<I, OUT> mock;

    public void setMock(TieredSequenceDataWriter<I, OUT> mock) {
        this.mock = mock;
    }

    @Override
    public void load(DataResource aData) throws ResourceInitializationException {
    }

    @Override
    public void write(JCas jCas, Annotation spanAnno, List<? extends I> seq, List<OUT[]> seqLabels)
            throws CleartkProcessingException {
        mock.write(jCas, spanAnno, seq, seqLabels);
    }

    @Override
    public void onCASChange(JCas cas) {
        mock.onCASChange(cas);
    }

    @Override
    public List<String> getTierIds() {
        return mock.getTierIds();
    }

    @Override
    public void close() throws IOException {
        mock.close();
    }
}
