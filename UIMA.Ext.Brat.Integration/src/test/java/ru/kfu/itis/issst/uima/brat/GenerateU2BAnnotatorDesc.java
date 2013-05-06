/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateU2BAnnotatorDesc {

	public static void main(String[] args) throws Exception {
		AnalysisEngineDescription anDesc = createPrimitiveDescription(UIMA2BratAnnotator.class);
		FileOutputStream fout = new FileOutputStream(
				"src/main/resources/ru/kfu/itis/issst/uima/brat/UIMA2BratAnnotator.xml");
		BufferedOutputStream out = new BufferedOutputStream(fout);
		try {
			anDesc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
		System.out.println("Done");
	}

}