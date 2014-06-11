/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.compare;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiFileListReader;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class WriteTables {

	public static void main(String[] args) throws Exception {
		Slf4jLoggerImpl.forceUsingThisImplementation();
		WriteTables instance = new WriteTables();
		new JCommander(instance, args);
		instance.run();
	}

	@Parameter(names = "--ds-config-file", required = true)
	private File dsConfigFile;
	@Parameter(names = "--xmi-dir", required = true)
	private File xmiDir;
	@Parameter(names = "--xmi-list-file", required = true)
	private File xmiListFile;
	@Parameter(names = "--name", required = true)
	private String taggingName;
	@Parameter(names = "--create-spans")
	private boolean createSpans = false;

	private WriteTables() {
	}

	private void run() throws Exception {
		TypeSystemDescription inputTS = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		//
		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createDescription(
				XmiFileListReader.class, inputTS,
				XmiFileListReader.PARAM_BASE_DIR, xmiDir,
				XmiFileListReader.PARAM_LIST_FILE, xmiListFile);
		//
		AnalysisEngineDescription tableWriterDesc = createPrimitiveDescription(
				TableWriter.class,
				TableWriter.PARAM_DATA_SOURCE_CONFIG_FILE, dsConfigFile.getPath(),
				TableWriter.PARAM_DISABLE_NEW_TEXT, !createSpans,
				TableWriter.PARAM_TAGGING_NAME, taggingName);
		//
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setReader(colReaderDesc);
		cpeBuilder.addAnalysisEngine(tableWriterDesc);
		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe, 10));
		cpe.process();
	}
}
