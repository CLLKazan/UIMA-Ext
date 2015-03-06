/**
 * 
 */
package ru.kfu.itis.cll.uima.consumer;

import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateXmiWriterDesc {

	public static void main(String[] args) throws Exception {
		AnalysisEngineDescription desc = AnalysisEngineFactory
				.createEngineDescription(XmiWriter.class);
		FileOutputStream out = new FileOutputStream(
				"src/main/resources/ru/kfu/itis/cll/uima/commons/an-XMIWriter.xml");
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

}