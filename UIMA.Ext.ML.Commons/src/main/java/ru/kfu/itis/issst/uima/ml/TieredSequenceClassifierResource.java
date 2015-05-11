package ru.kfu.itis.issst.uima.ml;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.cleartk.ml.CleartkProcessingException;
import ru.kfu.itis.cll.uima.util.UimaResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceClassifierResource<I extends AnnotationFS> extends Resource_ImplBase
        implements TieredSequenceClassifier<I, String> {

    /**
     * the base path used by engine descriptor that implements UIMA-Ext PoS-tagger API
     */
    public static final String RU_MODEL_BASE_PATH = "ru-pos-tagger";

    public static ExternalResourceDescription createDescription(File modelBaseDir) {
        return ExternalResourceFactory.createExternalResourceDescription(
                TieredSequenceClassifierResource.class,
                PARAM_MODEL_BASE_DIR, modelBaseDir.getPath());
    }

    public static ExternalResourceDescription createDescription(String modelBasePath) {
        return ExternalResourceFactory.createExternalResourceDescription(
                TieredSequenceClassifierResource.class,
                PARAM_MODEL_BASE_PATH, modelBasePath);
    }

    public static final String PARAM_MODEL_BASE_PATH = "modelBasePath";
    public static final String PARAM_MODEL_BASE_DIR = "modelBaseDir";
    // config
    @ConfigurationParameter(name = PARAM_MODEL_BASE_DIR, mandatory = false)
    private File modelBaseDir;
    @ConfigurationParameter(name = PARAM_MODEL_BASE_PATH, mandatory = false)
    private String modelBasePath;
    // aggregate
    private TieredSequenceClassifier<I, String> delegate;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
            throws ResourceInitializationException {
        if (!super.initialize(aSpecifier, aAdditionalParams))
            return false;
        if (modelBaseDir == null) {
            if (modelBasePath == null) {
                throw new IllegalStateException("Both modelBasePath & modelBaseDir are not specified");
            }
            try {
                modelBaseDir = UimaResourceUtils.resolveDirectory(modelBasePath, getResourceManager());
            } catch (Exception e) {
                throw new ResourceInitializationException(e);
            }
        }
        if (!modelBaseDir.isDirectory()) {
            throw new IllegalStateException(format(
                    "%s is not a directory", modelBaseDir));
        }
        //noinspection unchecked
        delegate = TieredSequenceClassifiers.fromModelBaseDir(modelBaseDir);
        return true;
    }

    @Override
    public List<String[]> classify(JCas jCas, Annotation spanAnno, List<? extends I> seq)
            throws CleartkProcessingException {
        return delegate.classify(jCas, spanAnno, seq);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void onCASChange(JCas cas) {
        delegate.onCASChange(cas);
    }

    @Override
    public List<String> getTierIds() {
        return delegate.getTierIds();
    }
}
