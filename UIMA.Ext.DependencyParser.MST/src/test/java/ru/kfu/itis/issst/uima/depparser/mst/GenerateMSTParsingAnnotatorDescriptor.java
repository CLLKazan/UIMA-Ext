/**
 * 
 */
package ru.kfu.itis.issst.uima.depparser.mst;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateMSTParsingAnnotatorDescriptor {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		if (args.length != 1) {
			System.err.println("Specify output dir!");
			System.exit(1);
		}
		File outputDir = new File(args[0]);
		File outputFile = new File(outputDir, "dep_parser.xml");

		AnalysisEngineDescription parserDesc = MSTParsingAnnotator.createDescription(
				new URL("file:mst-parser.model"));
		OutputStream out = FileUtils.openOutputStream(outputFile);
		try {
			parserDesc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

}
