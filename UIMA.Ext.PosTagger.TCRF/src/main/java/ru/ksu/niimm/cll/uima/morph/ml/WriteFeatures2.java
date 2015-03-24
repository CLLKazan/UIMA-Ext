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
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.util.DocumentUtils;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.cleartk.crfsuite2.CRFSuiteSerializedDataWriterFactory;
import ru.kfu.itis.issst.uima.ml.TieredSequenceDataWriterResource;
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import java.io.File;

import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils.getResourceManagerConfiguration;

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

    private WriteFeatures2() {
    }

    public void run() throws Exception {
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
        AnalysisEngineDescription tdExtractorDesc =
                createExtractorDescription(morphDictDesc, outputBaseDir);
        //
        cpeBuilder.addAnalysisEngine(tdExtractorDesc);
        // tune
        cpeBuilder.setMaxProcessingUnitThreatCount(
                Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
        //
        CollectionProcessingEngine cpe = cpeBuilder.createCpe();
        cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe, 50));
        cpe.process();
    }

    public static AnalysisEngineDescription createExtractorDescription(
            ExternalResourceDescription morphDictDesc,
            File outputBaseDir)
            throws ResourceInitializationException {
        // setup underlying data writer resource
        ExternalResourceDescription udwDesc = TieredSequenceDataWriterResource.createDescription(outputBaseDir,
                CRFSuiteSerializedDataWriterFactory.class);
        // setup training data writer
        AnalysisEngineDescription resultDesc = AnalysisEngineFactory.createEngineDescription(
                PosSequenceTrainingDataExtractor.class);
        // add other shared resources (ORDERing is important!)
        morphDictDesc.setName(PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME);
        getResourceManagerConfiguration(resultDesc).addExternalResource(morphDictDesc);
        getResourceManagerConfiguration(resultDesc).addExternalResource(udwDesc);
        // bind them
        ExternalResourceFactory.bindExternalResource(resultDesc,
                PosSequenceTrainingDataExtractor.RESOURCE_GRAM_MODEL, morphDictDesc.getName());
        ExternalResourceFactory.bindExternalResource(resultDesc,
                PosSequenceTrainingDataExtractor.RESOURCE_DATA_WRITER, udwDesc.getName());
        return resultDesc;
    }

    static {
        Slf4jLoggerImpl.forceUsingThisImplementation();
    }
}
