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

	public static final String inputFileXMIFile = "data/inputDir/test.xmi";
	
	public static final String inputFileDescFile = "desc/TestAnnotatorDescriptor.xml";

	public static final String DM_ENTITY_ANNOTATION_NAME = "ru.kfu.itis.cll.uima.commons.DocumentMetadata";

	public static final String encodingType = "UTF-8";

	public static void main(String[] args) throws IOException, UIMAException,
			CpeDescriptorException, SAXException {

		if (args.length != 2) {
			System.err.println("Usage: <input-file> <encoding>. "
					+ "Applying the new one ..." + inputFileTxtFile);
		}

		File inputFile = new File(inputFileXMIFile);

		if (!inputFile.isFile()) {
			System.err.println("Specified file does not exist");
			return;
		}

		String encoding = encodingType;

		// File outputDir = inputFile.getParentFile();

		// configure logging
		System.setProperty("logback.configurationFile", "resources/logback.xml");

		// configure AE
		// TODO seems ugly but works and does not
		// require to change descriptors for every
		// developer

		
		
		XMLInputSource aeDescInput = new XMLInputSource(inputFileDescFile);
		
	//	XMLInputSource aXMIInput   = new XMLInputSource(inputFileXMIFile);


		AnalysisEngineDescription aeDesc = UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(aeDescInput);

		// aeDesc.getAnalysisEngineMetaData().getConfigurationParameterSettings()
		// .setParameterValue("XmiOutputDir", outputDir.getPath());
		// create AE

		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeDesc);

		// prepare input
		String inputText = FileUtils.readFileToString(inputFile, encoding);

		// prepare input
		String inputXMI = FileUtils.readFileToString(inputFile, encoding);

		JCas cas = ae.newJCas();
		cas.setDocumentText(inputText);
		
		
		
		XmiCasDeserializer.deserialize(inputFile.toURI().toURL().openStream(), cas.getCas(), false);
		

		// Document metadata annotation
		DocumentMetadata inputMeta = new DocumentMetadata(cas);
		inputMeta.setSourceUri(inputFile.toURI().toString());
		System.out.println(inputFile.toURI().toString());
		inputMeta.addToIndexes();

		// Create test annotation or use created one's

		// Run
		long timeBefore = currentTimeMillis();

		     ae.process(cas);

		System.out.println("Finished in " + (currentTimeMillis() - timeBefore) + " ms");

		// String cpeDescPath = "desc/TestAnnotatorDescriptor.xml";
		// String inputDirPath = "data/inputDir";
		// String outputDirPath = "data/outputDir";
		// XMLInputSource cpeDescSource =
		// new XMLInputSource(cpeDescPath);
		// CpeDescription cpeDesc =
		// UIMAFramework.getXMLParser().parseCpeDescription(cpeDescSource);
		// // configure reader
		// cpeDesc.getAllCollectionCollectionReaders()[0]
		// .getConfigurationParameterSettings().setParameterValue("DirectoryPath",
		// inputDirPath);
		// // configure writer
		// cpeDesc.getCpeCasProcessors().getAllCpeCasProcessors()[0]
		// .getConfigurationParameterSettings()
		// .setParameterValue("XmiOutputDir", outputDirPath);
		// // produce
		// CollectionProcessingEngine cpe = UIMAFramework
		// .produceCollectionProcessingEngine(cpeDesc);
		// cpe.addStatusCallbackListener(new
		// ReportingStatusCallbackListener(cpe));
		// cpe.process();
	}
}
