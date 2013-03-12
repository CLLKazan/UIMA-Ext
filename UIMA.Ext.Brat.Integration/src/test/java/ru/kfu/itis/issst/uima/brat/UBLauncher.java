package ru.kfu.itis.issst.uima.brat;


import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLInputSource;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

public class UBLauncher {
	public static final String inputFileXMIDir           = "data/inputDir/news-corpora-for-ee-eval.cnews/";
	public static final String inputFileDescFile         = "desc/UIMA2BratAnnotatorDescriptor.xml";
	public static final String DM_ENTITY_ANNOTATION_NAME = "ru.kfu.itis.cll.uima.commons.DocumentMetadata";
	public static final String encodingType              = "UTF-8";

	public static void main(String[] args) throws IOException, UIMAException,
			CpeDescriptorException, SAXException {

		File inputFile = new File(inputFileXMIDir);

		if (!inputFile.isFile()) {
			if (!inputFile.isDirectory()) {
				System.err
						.println("Specified file or directory does not exist");
				return;
			}
		}

		// configure AE

		XMLInputSource aeDescInput = new XMLInputSource(inputFileDescFile);

		AnalysisEngineDescription aeDesc = UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(aeDescInput);

		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeDesc);

		String inputText = null;
		// if directory has files process them ...
		if (inputFile.listFiles().length > 0)
			for (File f : inputFile.listFiles()) {
				// prepare input
				inputText = FileUtils.readFileToString(f, encodingType);
				JCas cas = ae.newJCas();
				cas.setDocumentText(inputText);
				XmiCasDeserializer.deserialize(f.toURI().toURL().openStream(),cas.getCas(), false);
				// Create Document Metadata annotation
				DocumentMetadata 
				 inputMeta = new DocumentMetadata(cas);
				 inputMeta.setSourceUri(f.toURI().toString());
				 inputMeta.addToIndexes();
				// Create test annotation or use created one's
				ae.process(cas);
			}
	}
}
