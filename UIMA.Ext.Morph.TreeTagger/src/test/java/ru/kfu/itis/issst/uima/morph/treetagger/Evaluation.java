/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static java.lang.System.exit;
import static org.apache.uima.UIMAFramework.getXMLParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLInputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import com.google.common.collect.Maps;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.StatusCallbackListenerAdapter;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.eval.EvaluationLauncher;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;

/**
 * @author Rinat Gareev
 * 
 */
public class Evaluation {

	public static final String PLACEHOLDER_OUTPUT_BASE_DIR = "outputBaseDir";
	public static final String SYSPROP_SKIP_CPE = "skipCPE";
	private static final String AE_DESC_FILE = "desc/aggregates/eval-aggregate.xml";
	private static final String EVAL_PROPS_FILE = "desc/morph-tt-eval.properties";

	private final Logger log = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: <input-xmi-dir> <gold-xmi-dir> <outputBaseDir>");
			exit(1);
		}
		File inputXmiDir = dirMustExist(args[0]);
		File goldXmiDir = dirMustExist(args[1]);
		File outputBase = new File(args[2]);
		Slf4jLoggerImpl.forceUsingThisImplementation();
		new Evaluation(inputXmiDir, goldXmiDir, outputBase).run();
	}

	private static File dirMustExist(String path) {
		File dir = new File(path);
		if (!dir.isDirectory()) {
			System.err.println("Is not existing directory: " + dir);
			exit(1);
		}
		return dir;
	}

	private File aeDescFile = new File(AE_DESC_FILE);
	private File inputXmiDir;
	private File goldXmiDir;
	private File outputBase;
	private File evalPropertiesFile = new File(EVAL_PROPS_FILE);
	// derived
	private File xmiOutputDir;

	private Evaluation(File inputXmiDir, File goldXmiDir, File outputBase) {
		this.inputXmiDir = inputXmiDir;
		this.goldXmiDir = goldXmiDir;
		this.outputBase = outputBase;
		xmiOutputDir = new File(outputBase, "xmi");
	}

	private void run() throws Exception {
		if ("true".equalsIgnoreCase(System.getProperty(SYSPROP_SKIP_CPE))) {
			runEvaluation();
			return;
		}

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

	private AnalysisEngineDescription createAnalysisEngineDescription(
			File aeDescFile, String xmiOutputDir) throws IOException, UIMAException {
		XMLInputSource is = new XMLInputSource(aeDescFile);
		AnalysisEngineDescription desc;
		try {
			desc = getXMLParser().parseAnalysisEngineDescription(is);
		} finally {
			is.close();
		}
		ConfigurationParameterSettings aeParams = desc.getAnalysisEngineMetaData()
				.getConfigurationParameterSettings();
		aeParams.setParameterValue("XmiOutputDir", xmiOutputDir);
		return desc;
	}

	private CollectionReaderDescription createCollectionReaderDescription()
			throws IOException, UIMAException {
		TypeSystemDescription tsDesc = TypeSystemDescriptionFactory.createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
		return CollectionReaderFactory.createDescription(XmiCollectionReader.class,
				tsDesc,
				XmiCollectionReader.PARAM_INPUTDIR, inputXmiDir.getPath());
	}

	private class CpeListener extends StatusCallbackListenerAdapter {
		@Override
		public void collectionProcessComplete() {
			try {
				runEvaluation();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void runEvaluation() throws Exception {
		Properties evalProps = new Properties();
		Reader evalPropsReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(
						evalPropertiesFile), "utf-8"));
		try {
			evalProps.load(evalPropsReader);
		} finally {
			evalPropsReader.close();
		}
		// replace placeholders in evaluation config properties
		Map<String, String> phValues = Maps.newHashMap();
		phValues.put(PLACEHOLDER_OUTPUT_BASE_DIR, outputBase.getPath());
		ConfigPropertiesUtils.replacePlaceholders(evalProps, phValues);
		evalProps.setProperty("goldCasDirectory.dir", goldXmiDir.getPath());
		evalProps.setProperty("systemCasDirectory.dir", xmiOutputDir.getPath());
		if (log.isInfoEnabled()) {
			log.info("Evaluation config:\n {}", ConfigPropertiesUtils.prettyString(evalProps));
		}
		EvaluationLauncher.runUsingProperties(evalProps);
	}
}