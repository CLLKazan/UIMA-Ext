/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.xml.sax.SAXException;

import ru.kfu.cll.uima.segmentation.SentenceSplitter;
import ru.kfu.cll.uima.tokenizer.TokenizationAnnotator;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.SerializedDictionaryResource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateBasicAggregateDescriptor {

	/**
	 * @param args
	 * @throws ResourceInitializationException
	 */
	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		if (args.length != 1) {
			System.err.println("Usage: <path-to-dictionary-resource>");
			return;
		}
		String morphDictPath = args[0];
		URL morphDictUrl = new File(morphDictPath).toURI().toURL();
		ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				SerializedDictionaryResource.class, morphDictUrl);

		TypeSystemDescription tokenizerTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.jflex-tokenizer-ts");
		AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
				TokenizationAnnotator.class, tokenizerTsDesc);

		TypeSystemDescription ssTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
		AnalysisEngineDescription ssDesc = createPrimitiveDescription(SentenceSplitter.class,
				ssTsDesc);

		TypeSystemDescription morphTsDesc = createTypeSystemDescription("org.opencorpora.morphology-ts");
		AnalysisEngineDescription morphDesc = createPrimitiveDescription(MorphologyAnnotator.class,
				morphTsDesc,
				// dict resource
				MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, morphDictDesc);

		String outputPath = "src/test/resources/basic-aggregate.xml";
		AnalysisEngineDescription desc = AnalysisEngineFactory.createAggregateDescription(
				tokenizerDesc, ssDesc, morphDesc);
		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

}
