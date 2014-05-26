/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ConfigurableSerializedDictionaryResource;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@Parameters(separators = " =")
public class DictionaryComplianceCheckerBootstrap {

	public static void main(String[] args) throws Exception {
		DictionaryComplianceCheckerBootstrap instance = new DictionaryComplianceCheckerBootstrap();
		new JCommander(instance, args);
		// setup logging
		Slf4jLoggerImpl.forceUsingThisImplementation();
		instance.run();
	}

	@Parameter(names = { "-c", "--corpus-dir" }, required = true)
	private File corpusDir;
	@Parameter(names = { "-o", "--output-file" }, required = true)
	private File outFile;
	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> posCategories;

	private DictionaryComplianceCheckerBootstrap() {
	}

	private void run() throws Exception {
		//
		CollectionReaderDescription colReaderDesc;
		{
			TypeSystemDescription tsDesc = TypeSystemDescriptionFactory
					.createTypeSystemDescription(
							"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
							TokenizerAPI.TYPESYSTEM_TOKENIZER,
							"ru.kfu.itis.issst.uima.segmentation.segmentation-TypeSystem",
							"org.opencorpora.morphology-ts");
			//
			colReaderDesc = CollectionReaderFactory.createDescription(
					XmiCollectionReader.class,
					tsDesc,
					XmiCollectionReader.PARAM_INPUTDIR, corpusDir.getPath());
		}
		// 
		AnalysisEngineDescription dcCheckerDesc = createPrimitiveDescription(
				DictionaryComplianceChecker.class,
				DictionaryComplianceChecker.PARAM_OUT_FILE, outFile,
				DictionaryComplianceChecker.PARAM_TARGET_POS_CATEGORIES, posCategories);
		//
		ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				ConfigurableSerializedDictionaryResource.class, "file:dict.opcorpora.ser");
		ExternalResourceFactory.bindResource(dcCheckerDesc,
				DictionaryComplianceChecker.RESOURCE_DICTIONARY, morphDictDesc);
		// make AGGREGATE
		AnalysisEngineDescription aggregateDesc = createAggregateDescription(dcCheckerDesc);
		//
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setReader(colReaderDesc);
		cpeBuilder.addAnalysisEngine(aggregateDesc);
		cpeBuilder.setMaxProcessingUnitThreatCount(1);
		final CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		//
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe, 50));
		cpe.process();
	}
}