/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitter;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;
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

		Map<String, MetaDataObject> aeDescriptions = Maps.newLinkedHashMap();
		aeDescriptions.put("tokenizer", TokenizerAPI.getAEImport());
		//
		aeDescriptions.put("sentenceSplitter", SentenceSplitter.createDescription());
		//
		aeDescriptions.put("morphAnalyzer", MorphologyAnnotator.createDescription(morphDictDesc));
		//
		aeDescriptions.put("tag-assembler", TagAssembler.createDescription(morphDictDesc));
		AnalysisEngineDescription desc = PipelineDescriptorUtils
				.createAggregateDescription(aeDescriptions);

		String outputPath = "src/test/resources/basic-aggregate.xml";

		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
