/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphLauncher {

	/**
	 * @param args
	 * @throws IOException
	 * @throws InvalidXMLException
	 */
	public static void main(String[] args) throws IOException, UIMAException {
		if (args.length != 2) {
			System.err.println("Usage: <input-file> <encoding>");
			return;
		}
		String datapath = UIMAFramework.newDefaultResourceManager().getDataPath();
		File inputFile = new File(datapath + "/" + args[0]);
		if (!inputFile.isFile()) {
			System.err.println("Specified file does not exist");
			return;
		}
		String encoding = args[1];
		File outputDir = inputFile.getParentFile();

		// configure logging
		System.setProperty("logback.configurationFile",
				"ru/ksu/niimm/cll/uima/morph/opencorpora/logback.xml");

		// configure AE
		// TODO seems ugly but works and does not require to change descriptors for every
		// developer
		XMLInputSource aeDescInput = new XMLInputSource(
				"target/test-classes/opencorpora/ae-ru-test-MorphAnnotator.xml");
		AnalysisEngineDescription aeDesc = UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(aeDescInput);
		aeDesc.getAnalysisEngineMetaData().getConfigurationParameterSettings().setParameterValue(
				"XmiOutputDir", outputDir.getPath());

		// create AE
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeDesc);

		// prepare input
		String inputText = FileUtils.readFileToString(inputFile, encoding);
		JCas cas = ae.newJCas();
		cas.setDocumentText(inputText);
		DocumentMetadata inputMeta = new DocumentMetadata(cas);
		inputMeta.setSourceUri(inputFile.toURI().toString());
		inputMeta.addToIndexes();

		// run
		long timeBefore = currentTimeMillis();
		ae.process(cas);
		System.out.println("Finished in " + (currentTimeMillis() - timeBefore) + " ms");
	}

}