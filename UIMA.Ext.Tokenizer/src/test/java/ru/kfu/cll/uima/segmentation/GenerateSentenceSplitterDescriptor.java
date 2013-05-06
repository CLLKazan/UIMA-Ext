package ru.kfu.cll.uima.segmentation;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.FileOutputStream;
import java.io.IOException;

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
		String outputPath = "src/main/resources/ru/kfu/cll/uima/segmentation/ParagraphSplitter.xml";
		TypeSystemDescription tsDesc = createTypeSystemDescription("ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
		AnalysisEngineDescription desc = createPrimitiveDescription(ParagraphSplitter.class, tsDesc);
		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}