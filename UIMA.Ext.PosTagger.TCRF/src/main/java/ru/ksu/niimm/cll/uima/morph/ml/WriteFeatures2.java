/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ExternalResourceFactory;
import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;
import ru.kfu.itis.cll.uima.util.DocumentUtils;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils.getResourceManagerConfiguration;
import static ru.ksu.niimm.cll.uima.morph.ml.GramTiersFactory.tierSplitter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class WriteFeatures2 {

    public static void main(String[] args) throws Exception {
        WriteFeatures2 obj = new WriteFeatures2();
        JCommander com = new JCommander(obj);
        try {
            com.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            com.usage();
            System.exit(1);
        }
        obj.run();
    }

    // input - training set
    @Parameter(names = "--training-xmi-dir", required = true)
    private File trainingSetXmiDir;
    // output
    @Parameter(names = "--output-dir", required = true)
    private File outputBaseDir;
    @Parameter(names = {"--fec", "--feature-extraction-config"})
    private File featureExtractionCfg;

    private WriteFeatures2() {
    }

    public void run() throws Exception {
        Properties feCfg = IoUtils.readProperties(featureExtractionCfg);
        String tiersDef = ConfigPropertiesUtils.getStringProperty(feCfg, SimpleTieredFeatureExtractor.CFG_GRAM_TIERS);
        List<String> tierDefsList = newArrayList(tierSplitter.split(tiersDef));
        if (tierDefsList.isEmpty())
            throw new IllegalStateException("No gram tiers are defined");
        CpeBuilder cpeBuilder = new CpeBuilder();
        // setup TypeSystem
        TypeSystemDescription inputTS = createTypeSystemDescription(
                DocumentUtils.TYPESYSTEM_COMMONS,
                TokenizerAPI.TYPESYSTEM_TOKENIZER,
                SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
                PosTaggerAPI.TYPESYSTEM_POSTAGGER);
        // setup a training set collection reader
        CollectionReaderDescription colReaderDesc = XmiCollectionReader.createDescription(
                trainingSetXmiDir, inputTS);
        cpeBuilder.setReader(colReaderDesc);
        // setup a morph dictionary
        ExternalResourceDescription morphDictDesc = MorphDictionaryAPIFactory
                .getMorphDictionaryAPI()
                .getResourceDescriptionForCachedInstance();
        // setup underlying data writer resource
        ExternalResourceDescription udwDesc = TieredSequenceDataWriterResource.createDescription(outputBaseDir);
        // setup training data writer
        AnalysisEngineDescription dataWriterDesc = AnalysisEngineFactory.createPrimitiveDescription(
                PosSequenceTrainingDataExtractor.class,
                PosSequenceTrainingDataExtractor.PARAM_TIERS, tierDefsList,
                PosSequenceTrainingDataExtractor.RESOURCE_DATA_WRITER, udwDesc);
        // add other shared resources
        morphDictDesc.setName(PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME);
        getResourceManagerConfiguration(dataWriterDesc).addExternalResource(morphDictDesc);
        // bind them
        ExternalResourceFactory.bindExternalResource(dataWriterDesc,
                PosSequenceTrainingDataExtractor.RESOURCE_GRAM_MODEL, morphDictDesc.getName());
        ExternalResourceFactory.bindExternalResource(dataWriterDesc,
                TieredSequenceDataWriterResource.MORPH_DICT_LOOKUP_KEY, morphDictDesc.getName());
        //
        cpeBuilder.addAnalysisEngine(dataWriterDesc);
        //
        CollectionProcessingEngine cpe = cpeBuilder.createCpe();
        cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe, 50));
        cpe.process();
    }

    static {
        Slf4jLoggerImpl.forceUsingThisImplementation();
    }
}
