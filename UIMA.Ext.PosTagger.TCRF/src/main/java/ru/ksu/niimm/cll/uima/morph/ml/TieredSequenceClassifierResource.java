package ru.ksu.niimm.cll.uima.morph.ml;

import com.beust.jcommander.internal.Lists;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.jar.JarClassifierBuilder;
import org.uimafit.component.Resource_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ExternalResourceFactory;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.CachedResourceTuple;
import ru.kfu.itis.issst.cleartk.JarSequenceClassifierFactory;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory.getMorphDictionaryAPI;
import static ru.ksu.niimm.cll.uima.morph.ml.TieredSequenceDataWriterResource.FILENAME_FEATURE_EXTRACTION_CONFIG;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceClassifierResource extends Resource_ImplBase implements SequenceClassifier<String> {

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
    private CachedResourceTuple<MorphDictionary> morphDictTuple;
    private List<org.cleartk.classifier.SequenceClassifier<String>> classifiers;
    private SimpleTieredFeatureExtractor featureExtractor;
    private TieredSequenceClassifier delegate;

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
                URL modelBaseURL = getResourceManager().resolveRelativePath(modelBasePath);
                if (modelBaseURL == null)
                    throw new IllegalStateException(format(
                            "Can't resolve path %s using an UIMA relative path resolver", modelBasePath));
                modelBaseDir = new File(modelBaseURL.toURI());
            } catch (Exception e) {
                throw new ResourceInitializationException(e);
            }

        }
        if (!modelBaseDir.isDirectory()) {
            throw new IllegalStateException(format(
                    "%s is not a directory", modelBaseDir
            ));
        }
        try {
            initialize();
        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
        return true;
    }

    void initialize() throws Exception {
        initDictionary();
        initFeatureExtractor();
        initUnderlyingClassifiers();
        delegate = new TieredSequenceClassifier() {{
            this.classifiers = TieredSequenceClassifierResource.this.classifiers;
            this.featureExtractor = TieredSequenceClassifierResource.this.featureExtractor;
        }};
    }

    private void initDictionary() throws Exception {
        morphDictTuple = getMorphDictionaryAPI().getCachedInstance();
    }

    private void initFeatureExtractor() throws IOException {
        File feCfgFile = new File(modelBaseDir, FILENAME_FEATURE_EXTRACTION_CONFIG);
        Properties featExtractionCfg = IoUtils.readProperties(feCfgFile);
        // TODO refactor implementation-specific logic
        featureExtractor = SimpleTieredFeatureExtractor.from(featExtractionCfg);
        featureExtractor.initialize(morphDictTuple.getResource());
    }

    private void initUnderlyingClassifiers() throws IOException {
        classifiers = Lists.newArrayList();
        GramTiers gramTiers = featureExtractor.getGramTiers();
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            String tierId = gramTiers.getTierId(tier);
            File tierModelDir = new File(modelBaseDir, tierId);
            File tierModelJar = JarClassifierBuilder.getModelJarFile(tierModelDir);
            JarSequenceClassifierFactory<String> clFactory = new JarSequenceClassifierFactory<String>();
            clFactory.setClassifierJarPath(tierModelJar.getPath());
            org.cleartk.classifier.SequenceClassifier<String> cl = clFactory.createClassifier();
            classifiers.add(cl);
        }
    }

    @Override
    public List<String> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq)
            throws CleartkProcessingException {
        return delegate.classify(jCas, spanAnno, seq);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
