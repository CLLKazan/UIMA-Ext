/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateOpenNLPPosTaggerDesc {

	public static void main(String[] args)
			throws ResourceInitializationException, IOException, SAXException {
		if (args.length != 2) {
			System.err.println("Usage: <model-url> <xml-desc-output-path>");
			System.exit(1);
		}
		String modelUrl = args[0];
		// check
		System.out.println("Using modelUrl:" + new URL(modelUrl));
		File outputFile = new File(args[1]);
		AnalysisEngineDescription taggerDesc = OpenNLPPosTagger.createDescription(modelUrl);
		FileOutputStream out = FileUtils.openOutputStream(outputFile);
		try {
			taggerDesc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
