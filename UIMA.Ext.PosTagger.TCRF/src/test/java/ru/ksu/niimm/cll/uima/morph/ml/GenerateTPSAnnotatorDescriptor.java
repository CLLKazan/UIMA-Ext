/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateTPSAnnotatorDescriptor {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		if (args.length != 1) {
			System.err.println("Provide modelBaseDir as the only argument!");
			System.exit(1);
		}
		File modelBaseDir = new File(args[0]);
		AnalysisEngineDescription taggerDesc = TieredPosSequenceAnnotatorFactory
				.createTaggerDescription(modelBaseDir);
		final String descriptorFileName = TieredPosSequenceAnnotatorFactory.POS_TAGGER_DESCRIPTOR_NAME
				+ ".xml";
		File descriptorFile = new File(modelBaseDir, descriptorFileName);
		OutputStream out = openOutputStream(descriptorFile);
		try {
			taggerDesc.toXML(out);
		} finally {
			closeQuietly(out);
		}
		System.out.println("Produced: " + descriptorFile);
	}

}
