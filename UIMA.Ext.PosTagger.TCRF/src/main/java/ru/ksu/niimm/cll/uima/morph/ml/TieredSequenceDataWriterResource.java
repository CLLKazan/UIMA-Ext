package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.Lists;
import org.apache.uima.fit.component.Resource_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.cleartk.ml.CleartkProcessingException;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.issst.cleartk.crfsuite2.CRFSuiteSerializedDataWriter;
import ru.kfu.itis.issst.uima.ml.SequenceDataWriter;
import ru.kfu.itis.issst.uima.ml.TieredFeatureExtractor;
import ru.kfu.itis.issst.uima.ml.TieredFeatureExtractors;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.lang.String.format;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceDataWriterResource extends Resource_ImplBase
        implements SequenceDataWriter<Token, String[]> {

    public static ExternalResourceDescription createDescription(File outputBaseDir) {
        return ExternalResourceFactory.createExternalResourceDescription(TieredSequenceDataWriterResource.class,
                PARAM_OUTPUT_BASE_DIR, outputBaseDir.getPath());
    }

    public static final String PARAM_OUTPUT_BASE_DIR = "outputBaseDir";

    @ConfigurationParameter(name = PARAM_OUTPUT_BASE_DIR, mandatory = true)
    private File outputBaseDir;
    private Properties featureExtractionCfg;
    // aggregate
    private TieredFeatureExtractor<Token, String> featureExtractor;
    private List<org.cleartk.ml.SequenceDataWriter<String>> dataWriters;
    // delegate
    private TieredSequenceDataWriter<Token> delegate;

    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams)
            throws ResourceInitializationException {
        if (!super.initialize(aSpecifier, aAdditionalParams))
            return false;
        if (outputBaseDir.exists() && !outputBaseDir.isDirectory()) {
            throw new IllegalStateException(format("%s exists but it is not a directory", outputBaseDir));
        }
        File feCfgFile = new File(outputBaseDir, TieredFeatureExtractors.FILENAME_FEATURE_EXTRACTION_CONFIG);
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
        delegate = new TieredSequenceDataWriter<Token>() {
            {
                this.dataWriters = TieredSequenceDataWriterResource.this.dataWriters;
                this.featureExtractor = TieredSequenceDataWriterResource.this.featureExtractor;
            }
        };
    }

    private void initFeatureExtractor() throws ResourceInitializationException {
        this.featureExtractor = TieredFeatureExtractors.from(featureExtractionCfg);
    }

    private void initUnderlyingDataWriters() throws ResourceInitializationException {
        GramTiers gramTiers = getGramTiers();
        List<org.cleartk.ml.SequenceDataWriter<String>> dataWriters = Lists.newArrayList();
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            String tierId = gramTiers.getTierId(tier);
            // TODO avoid binding to the specific implementation
            File tierOutDir = new File(outputBaseDir, tierId);
            CRFSuiteSerializedDataWriter dw;
            try {
                dw = new CRFSuiteSerializedDataWriter(tierOutDir);
            } catch (IOException e) {
                throw new ResourceInitializationException(e);
            }
            dataWriters.add(dw);
        }
        this.dataWriters = dataWriters;
    }

    private GramTiers getGramTiers() {
        return ((SimpleTieredFeatureExtractor) featureExtractor).getGramTiers();
    }

    @Override
    public void write(JCas jCas, Annotation spanAnno, List<? extends Token> seq, List<String[]> seqLabels)
            throws CleartkProcessingException {
        delegate.write(jCas, spanAnno, seq, seqLabels);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
