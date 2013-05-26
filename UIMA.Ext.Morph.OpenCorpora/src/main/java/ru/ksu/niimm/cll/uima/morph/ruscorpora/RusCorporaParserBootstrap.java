/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static java.lang.System.exit;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class RusCorporaParserBootstrap {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: <ruscorpora-text-dir> <output-xmi-dir>");
			exit(1);
		}
		File ruscorporaTextDir = new File(args[0]);
		if (!ruscorporaTextDir.isDirectory()) {
			System.err.println(String.format("%s is not existing directory", ruscorporaTextDir));
			exit(1);
		}
		String xmiOutputDir = args[1];
		// setup logging
		Slf4jLoggerImpl.forceUsingThisImplementation();
		// TypeSystem 
		TypeSystemDescription tsDesc = TypeSystemDescriptionFactory.createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		//
		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createDescription(
				RusCorporaCollectionReader.class,
				tsDesc,
				RusCorporaCollectionReader.PARAM_INPUT_DIR, ruscorporaTextDir.getPath(),
				RusCorporaCollectionReader.PARAM_TAG_MAPPER_CLASS,
				IdentityTagTagger.class.getName());
		// 
		AnalysisEngineDescription xmiWriterDesc = AnalysisEngineFactory.createPrimitiveDescription(
				XmiWriter.class,
				XmiWriter.PARAM_OUTPUTDIR, xmiOutputDir);
		// 
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setReader(colReaderDesc);
		cpeBuilder.addAnalysisEngine(xmiWriterDesc);
		cpeBuilder.setMaxProcessingUnitThreatCount(3);
		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		//
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(
				cpe, 50));
		cpe.process();
	}
}