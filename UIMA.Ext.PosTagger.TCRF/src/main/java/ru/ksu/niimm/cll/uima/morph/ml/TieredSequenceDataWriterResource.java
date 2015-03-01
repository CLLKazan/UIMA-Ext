package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.Lists;
import org.apache.uima.resource.*;
import org.uimafit.factory.ExternalResourceFactory;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeDataWriter;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import static java.lang.String.format;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceDataWriterResource extends TieredSequenceDataWriter implements SharedResourceObject {

    public static ExternalResourceDescription createDescription(File outputBaseDir) {
        return ExternalResourceFactory.createExternalResourceDescription(
                TieredSequenceDataWriterResource.class,
                outputBaseDir);
    }

    /**
     * Key for a pipeline builder to bind a morph dictionary in a ResourceManager instance.
     */
    public static final String MORPH_DICT_LOOKUP_KEY =
            "morph/dicts/" + SimpleTieredFeatureExtractor.RESOURCE_MORPH_DICTIONARY;

    public static final String FILENAME_FEATURE_EXTRACTION_CONFIG = "fec.properties";

    private File outputBaseDir;
    private Properties featureExtractionCfg;
    private ResourceManager resourceManager;

    @Override
    public void load(DataResource aData) throws ResourceInitializationException {
        URI baseDirUri = aData.getUri();
        outputBaseDir = new File(baseDirUri);
        if (outputBaseDir.exists() && !outputBaseDir.isDirectory()) {
            throw new IllegalStateException(format("%s exists but it is not a directory", outputBaseDir));
        }
        File feCfgFile = new File(outputBaseDir, FILENAME_FEATURE_EXTRACTION_CONFIG);
        try {
            featureExtractionCfg = IoUtils.readProperties(feCfgFile);
        } catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
        resourceManager = aData.getResourceManager();
        initialize();
    }

    private void initialize() throws ResourceInitializationException {
        initFeatureExtractor();
        initUnderlyingDataWriters();
    }

    private void initFeatureExtractor() throws ResourceInitializationException {
        SimpleTieredFeatureExtractor fe = SimpleTieredFeatureExtractor.from(featureExtractionCfg);
        try {
            fe.initialize(getMorphDictionary());
        } catch (ResourceAccessException e) {
            throw new ResourceInitializationException(e);
        }
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

    private MorphDictionary getMorphDictionary() throws ResourceAccessException {
        String dictQualifiedKey = "/" + MORPH_DICT_LOOKUP_KEY;
        MorphDictionary morphDict = (MorphDictionary) resourceManager.getResource(
                // TODO elaborate naming in this global space
                dictQualifiedKey);
        if (morphDict == null) {
            throw new IllegalStateException("MorphDictionary is not registerd under key " + dictQualifiedKey);
        }
        return morphDict;
    }

    private GramTiers getGramTiers() {
        return ((SimpleTieredFeatureExtractor) featureExtractor).getGramTiers();
    }
}
