/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.io.File;
import java.util.List;

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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Joiner;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ConfigurableSerializedDictionaryResource;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeDataWriterFactory;

/**
 * @author Rinat Gareev
 * 
 */
public class WriteTieredCRFSuiteTraining {

	public static void main(String[] args) throws Exception {
		WriteTieredCRFSuiteTraining instance = new WriteTieredCRFSuiteTraining();
		new JCommander(instance, args);
		instance.run();
	}

	@Parameter(names = "-c", required = true)
	private File corpusXmiDir;
	@Parameter(names = "-o", required = true)
	private File outputDir;
	@Parameter(names = "-p", required = true, variableArity = true)
	private List<String> posTiers;

	private void run() throws Exception {
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
						TieredPosSequenceAnnotator.class,
						TieredPosSequenceAnnotator.PARAM_POS_TIERS, posTiers,
						TieredPosSequenceAnnotator.PARAM_CURRENT_TIER, posTiers.size() - 1,
						TieredPosSequenceAnnotator.PARAM_IS_TRAINING, true,
						TieredPosSequenceAnnotator.PARAM_DATA_WRITER_FACTORY_CLASS_NAME,
						CRFSuiteStringOutcomeDataWriterFactory.class.getName(),
						DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
						new File(outputDir, tiers2directoryNameJoiner.join(posTiers)).getPath());
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

	private static final Joiner tiers2directoryNameJoiner = Joiner.on('_');
}