/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static java.lang.System.exit;
import static org.apache.uima.UIMAFramework.getResourceSpecifierFactory;
import static org.apache.uima.UIMAFramework.getXMLParser;
import static org.apache.uima.UIMAFramework.newDefaultResourceManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.util.XMLInputSource;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;

/**
 * @author Rinat Gareev
 * 
 */
public class Evaluation {
	
	public static final String PLACEHOLDER_OUTPUT_BASE_DIR = "outputBaseDir";
	public static final String SYSPROP_SKIP_CPE = "skipCPE";

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: <outputBaseDir>");
			exit(1);
		}
		File outputBase = new File(args[0]);
		File xmiOutputDir = new File(outputBase, "xmi");

		if ("true".equals(System.getProperty(SYSPROP_SKIP_CPE))) {
			runEvaluation();
			return;
		}
		xmiOutputDir.mkdir();

		CollectionReaderDescription colReaderDesc = createCollectionReaderDescription();
		AnalysisEngineDescription aeDesc = createAnalysisEngineDescription(
				aeDescFile, xmiOutputDir.getPath());

		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setReader(colReaderDesc);
		cpeBuilder.setMaxProcessingUnitThreatCount(3);
		cpeBuilder.addAnalysisEngine(aeDesc);
		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new CpeListener());
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe));

		cpe.process();
	}
	
	private static CollectionReaderDescription createCollectionReaderDescription()
			throws IOException, UIMAException {
		// TODO
		return null;
	}

}