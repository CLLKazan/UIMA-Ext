/**
 * 
 */
package ru.kfu.itis.cll.uima.annotator;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateFeatureValueReplacerDesc {

	/**
	 * @param args
	 * @throws UIMAException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws UIMAException, SAXException, IOException {
		AnalysisEngineDescription desc = AnalysisEngineFactory
				.createPrimitiveDescription(FeatureValueReplacer.class);
		desc.toXML(new FileOutputStream(
				"src/main/resources/ru/kfu/itis/cll/uima/annotator/FeatureValueReplacer.xml"));
	}

}