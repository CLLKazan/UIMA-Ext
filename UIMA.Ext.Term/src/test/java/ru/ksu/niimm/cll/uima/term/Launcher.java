/**
 * 
 */
package ru.ksu.niimm.cll.uima.term;

import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;


import org.apache.commons.io.FileUtils;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;


/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Launcher {

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
		File inputFile = new File(args[0]);
		if (!inputFile.isFile()) {
			System.err.println("Specified file does not exist");
			return;
		}
		String encoding = args[1];
	
		File outputDir = inputFile.getParentFile();

		 String p = "data";
		 
				
		// configure AE
		// TODO seems ugly but works and does not require to change descriptors for every
		// developer
		XMLInputSource aeDescInput = new XMLInputSource(
				"/home/vladimir/workspace-git/uima-ext/UIMA-Ext/UIMA.Ext.Term/desc/ae-ru-test-Loc.xml");
		        
		AnalysisEngineDescription aeDesc = UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(aeDescInput);
		
		

		// create AE
		AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(aeDesc);
		
		
				
		// prepare input
		String inputText = FileUtils.readFileToString(inputFile, encoding);
		
		CAS cas = ae.newCAS();
		cas.setDocumentLanguage("x-unspecified");	
		cas.setDocumentText(inputText);
		
		
		
		/*JCas jcas = cas.getJCas();
		DocumentMetadata inputMeta = new DocumentMetadata(jcas);
		inputMeta.setSourceUri(inputFile.toURI().toString());
		inputMeta.addToIndexes();*/

	    // run
		long timeBefore = currentTimeMillis();
		
		ae.process(cas);
		
		 File outFile = new File(p+"testData.txt");
		    File outFileRef = new File(p+"testDataRef.txt");
		
		//cas.createAnnotation(Locution, begin, end)
		System.out.println(cas.getDocumentAnnotation().getCoveredText());
		
		

		System.out.println("Finished in " + (currentTimeMillis() - timeBefore) + " ms");
		
	}

}
