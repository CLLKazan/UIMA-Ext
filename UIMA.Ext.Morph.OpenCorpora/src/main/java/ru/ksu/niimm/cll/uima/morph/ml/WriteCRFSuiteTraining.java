/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static java.lang.System.exit;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.cleartk.classifier.jar.DirectoryDataWriterFactory;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ConfigurableSerializedDictionaryResource;

/**
 * @author Rinat Gareev
 * 
 */
public class WriteCRFSuiteTraining {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: <corpusXmiDir> <outputDir>");
			exit(1);
		}
		File corpusXmiDir = new File(args[0]);
		File outputDir = new File(args[1]);

		TypeSystemDescription tsDesc = TypeSystemDescriptionFactory.createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createDescription(
				XmiCollectionReader.class, tsDesc,
				XmiCollectionReader.PARAM_INPUTDIR, corpusXmiDir.getPath());
		AnalysisEngineDescription trainingSetExtractorDesc = AnalysisEngineFactory
				.createPrimitiveDescription(
						CRFSuitePosSequenceAnnotator.class,
						CRFSuitePosSequenceAnnotator.PARAM_IS_TRAINING, true,
						CRFSuitePosSequenceAnnotator.PARAM_DATA_WRITER_FACTORY_CLASS_NAME,
						CRFSuiteStringOutcomeDataWriterFactory.class.getName(),
						DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY, outputDir.getPath());
		ExternalResourceDescription morphDictHolderDesc = ExternalResourceFactory
				.createExternalResourceDescription(
						ConfigurableSerializedDictionaryResource.class, "file:dict.opcorpora.ser");
		ExternalResourceFactory.bindResource(trainingSetExtractorDesc,
				CRFSuitePosSequenceAnnotator.RESOURCE_KEY_MORPH_DICTIONARY, morphDictHolderDesc);
		//
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setReader(colReaderDesc);
		cpeBuilder.addAnalysisEngine(trainingSetExtractorDesc);
		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe, 50));

		cpe.process();
	}
}