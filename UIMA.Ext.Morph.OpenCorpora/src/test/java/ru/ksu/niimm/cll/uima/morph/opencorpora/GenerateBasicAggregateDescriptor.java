/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.xml.sax.SAXException;

import ru.kfu.cll.uima.segmentation.SentenceSplitter;
import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.SerializedDictionaryResource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateBasicAggregateDescriptor {

	public static final String MORPH_DICT_URL = "file:dict.opcorpora.ser";

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		// NOTE! A file URL for generated SerializedDictionaryResource description assumes 
		// that the required dictionary file is within one of UIMA datapath folders.
		// So users of the generated aggregate descriptor should setup 'uima.datapath' properly .
		ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				SerializedDictionaryResource.class, MORPH_DICT_URL);

		TypeSystemDescription tokenizerTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem");
		AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
				InitialTokenizer.class, tokenizerTsDesc);

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
