package ru.kfu.itis.issst.uima.brat;

import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;

public class UBLauncher {

	public static final String inputFileTxtFile = "data/inputDir/test.txt";

	public static final String inputFileXMIFile = "data/inputDir/cn_5.txt.xmi";

	public static final String inputFileXMIDir  = "data/inputDir/news-corpora-for-ee-eval.cnews/";

	public static final String inputFileDescFile = "desc/TestAnnotatorDescriptor.xml";

	public static final String DM_ENTITY_ANNOTATION_NAME = "ru.kfu.itis.cll.uima.commons.DocumentMetadata";

	public static final String encodingType = "UTF-8";

	public static void main(String[] args) throws IOException, UIMAException,
			CpeDescriptorException, SAXException {

		// configure logging
		System.setProperty("logback.configurationFile", "resources/logback.xml");

		if (args.length != 2) {
			System.err.println("Usage: <input-file> <encoding>. "
					+ "Opening the new directory:" + inputFileXMIDir);
		}

		File inputFile = new File(inputFileXMIDir);

		if (!inputFile.isFile()) {
			if (!inputFile.isDirectory()){
				System.err
						.println("Specified file or directory does not exist");
			return;
			}
		}

		// configure AE
		// TODO seems ugly but works and does not
		// require to change descriptors for every
		// developer

		XMLInputSource aeDescInput = new XMLInputSource(inputFileDescFile);

		// XMLInputSource aXMIInput = new XMLInputSource(inputFileXMIFile);

		AnalysisEngineDescription aeDesc = UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(aeDescInput);

		// aeDesc.getAnalysisEngineMetaData().getConfigurationParameterSettings()
		// .setParameterValue("XmiOutputDir", outputDir.getPath());
		// create AE

		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeDesc);

		// Run
		long timeBefore = currentTimeMillis();

		String inputText = null;
		// if directory has files process them ...
		if (inputFile.listFiles().length > 0)
			for (File f : inputFile.listFiles()) {
				// prepare input
				inputText = FileUtils.readFileToString(f, encodingType);

				JCas cas = ae.newJCas();
				cas.setDocumentText(inputText);

				XmiCasDeserializer.deserialize(f.toURI().toURL()
						.openStream(), cas.getCas(), false);
				// Create Document Metadata annotation
				DocumentMetadata inputMeta = new DocumentMetadata(cas);
				inputMeta.setSourceUri(f.toURI().toString());
				System.out.println(f.toURI().toString());
				inputMeta.addToIndexes();

				// Create test annotation or use created one's

				ae.process(cas);
			}

		System.out.println("Finished in " + (currentTimeMillis() - timeBefore)
				+ " ms");

	}
}
