package ru.kfu.itis.issst.uima.segmentation;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateSentenceSplitterDescriptor {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		String outputPath = "src/main/resources/ru/kfu/itis/issst/uima/segmentation/sentence-splitter-ae.xml";
		TypeSystemDescription tsDesc = SentenceSplitterAPI.getTypeSystemDescription();
		AnalysisEngineDescription desc = createPrimitiveDescription(SentenceSplitter.class, tsDesc);
		FileOutputStream out = FileUtils.openOutputStream(new File(outputPath));
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}