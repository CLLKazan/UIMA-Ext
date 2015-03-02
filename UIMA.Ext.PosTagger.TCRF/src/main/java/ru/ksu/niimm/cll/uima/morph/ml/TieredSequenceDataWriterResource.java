package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.Lists;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.cleartk.classifier.CleartkProcessingException;
import org.uimafit.component.Resource_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ExternalResourceFactory;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.CacheKey;
import ru.kfu.itis.cll.uima.util.CachedResourceTuple;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeDataWriter;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;
import static ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory.getMorphDictionaryAPI;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceDataWriterResource extends Resource_ImplBase implements SequenceDataWriter<String[]> {

    public static ExternalResourceDescription createDescription(File outputBaseDir) {
        return ExternalResourceFactory.createExternalResourceDescription(TieredSequenceDataWriterResource.class,
                PARAM_OUTPUT_BASE_DIR, outputBaseDir.getPath());
    }

    public static final String PARAM_OUTPUT_BASE_DIR = "outputBaseDir";

    public static final String FILENAME_FEATURE_EXTRACTION_CONFIG = "fec.properties";

    @ConfigurationParameter(name = PARAM_OUTPUT_BASE_DIR, mandatory = true)
    private File outputBaseDir;
    private Properties featureExtractionCfg;
    // aggregate
    private TieredFeatureExtractor featureExtractor;
    private List<org.cleartk.classifier.SequenceDataWriter<String>> dataWriters;
    // delegate
    @SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
    private CacheKey morphDictCacheKey;
    private MorphDictionary morphDictionary;
    private TieredSequenceDataWriter delegate;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
            throws ResourceInitializationException {
        if (!super.initialize(aSpecifier, aAdditionalParams))
            return false;
        if (outputBaseDir.exists() && !outputBaseDir.isDirectory()) {
            throw new IllegalStateException(format("%s exists but it is not a directory", outputBaseDir));
        }
        File feCfgFile = new File(outputBaseDir, FILENAME_FEATURE_EXTRACTION_CONFIG);
        try {
            featureExtractionCfg = IoUtils.readProperties(feCfgFile);
        } catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        initialize();
        return true;
    }

    private void initialize() throws ResourceInitializationException {
        initFeatureExtractor();
        initUnderlyingDataWriters();
        delegate = new TieredSequenceDataWriter() {
            {
                this.dataWriters = TieredSequenceDataWriterResource.this.dataWriters;
                this.featureExtractor = TieredSequenceDataWriterResource.this.featureExtractor;
            }
        };
    }

    private void initFeatureExtractor() throws ResourceInitializationException {
        initMorphDictionary();
        SimpleTieredFeatureExtractor fe = SimpleTieredFeatureExtractor.from(featureExtractionCfg);
        fe.initialize(morphDictionary);
        this.featureExtractor = fe;
    }

    private void initUnderlyingDataWriters() throws ResourceInitializationException {
        GramTiers gramTiers = getGramTiers();
        List<org.cleartk.classifier.SequenceDataWriter<String>> dataWriters = Lists.newArrayList();
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            String tierId = gramTiers.getTierId(tier);
            // TODO avoid binding to the specific implementation
            File tierOutDir = new File(outputBaseDir, tierId);
            CRFSuiteStringOutcomeDataWriter dw;
            try {
                dw = new CRFSuiteStringOutcomeDataWriter(tierOutDir);
            } catch (FileNotFoundException e) {
                throw new ResourceInitializationException(e);
            }
            dataWriters.add(dw);
        }
        this.dataWriters = dataWriters;
    }

    private void initMorphDictionary() throws ResourceInitializationException {
        CachedResourceTuple<MorphDictionary> t;
        try {
            t = getMorphDictionaryAPI().getCachedInstance();
        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
        morphDictCacheKey = t.getCacheKey();
        morphDictionary = t.getResource();
    }

    private GramTiers getGramTiers() {
        return ((SimpleTieredFeatureExtractor) featureExtractor).getGramTiers();
    }

    @Override
    public void write(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq, List<String[]> seqLabels) throws CleartkProcessingException {
        delegate.write(jCas, spanAnno, seq, seqLabels);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
