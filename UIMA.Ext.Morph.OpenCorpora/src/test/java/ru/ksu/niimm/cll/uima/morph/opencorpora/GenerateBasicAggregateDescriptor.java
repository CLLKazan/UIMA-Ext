/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.xml.sax.SAXException;

import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
import ru.kfu.cll.uima.tokenizer.PostTokenizer;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ConfigurableSerializedDictionaryResource;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DummyWordformPredictor;

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
				ConfigurableSerializedDictionaryResource.class, MORPH_DICT_URL,
				ConfigurableSerializedDictionaryResource.PARAM_PREDICTOR_CLASS_NAME,
				DummyWordformPredictor.class.getName());

		AnalysisEngineDescription desc = createAggregateDescription(
				InitialTokenizer.createDescription(),
				PostTokenizer.createDescription(),
				SentenceSplitter.createDescription(),
				MorphologyAnnotator.createDescription(morphDictDesc),
				TagAssembler.createDescription(morphDictDesc));

		String outputPath = "src/test/resources/basic-aggregate.xml";

		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
