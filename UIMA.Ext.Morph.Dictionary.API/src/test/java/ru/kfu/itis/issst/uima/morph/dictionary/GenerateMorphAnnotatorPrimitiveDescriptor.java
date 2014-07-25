package ru.kfu.itis.issst.uima.morph.dictionary;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.uima.morph.dictionary.MorphologyAnnotator;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateMorphAnnotatorPrimitiveDescriptor {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		String outputPath = "src/main/resources/ru/kfu/itis/issst/uima/morph/dictionary/MorphologyAnnotator.xml";
		TypeSystemDescription tsDesc = createTypeSystemDescription("org.opencorpora.morphology-ts");
		AnalysisEngineDescription desc = createPrimitiveDescription(MorphologyAnnotator.class, tsDesc);
		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}