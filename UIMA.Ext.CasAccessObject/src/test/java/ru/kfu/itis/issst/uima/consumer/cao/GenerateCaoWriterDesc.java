/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateCaoWriterDesc {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		AnalysisEngineDescription anDesc = createEngineDescription(CAOWriter.class);
		FileOutputStream out = new FileOutputStream(
				"src/main/resources/ru/kfu/itis/issst/uima/consumer/cao/CAOWriter.xml");
		anDesc.toXML(new BufferedOutputStream(out));
		System.out.println("Done");
	}

}