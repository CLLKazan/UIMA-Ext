/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger;

import static org.apache.uima.fit.factory.ExternalResourceFactory.bindExternalResource;
import static ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory.getMorphDictionaryAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.morph.commons.GramModelBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.kfu.itis.issst.uima.morph.dictionary.MorphologyAnnotator;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateAggregateDescriptorForMorphAnnotator {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		if (args.length != 1) {
			System.err.println("Usage: <output-path>");
			System.exit(1);
		}
		String outputPath = args[0];
		// NOTE! A file URL for generated SerializedDictionaryResource description assumes 
		// that the required dictionary file is within one of UIMA datapath folders.
		// So users of the generated aggregate descriptor should setup 'uima.datapath' properly .
		ExternalResourceDescription morphDictDesc = getMorphDictionaryAPI()
				.getResourceDescriptionWithPredictorEnabled();

		Map<String, MetaDataObject> aeDescriptions = Maps.newLinkedHashMap();
		aeDescriptions.put("tokenizer", TokenizerAPI.getAEImport());
		//
		aeDescriptions.put("sentence-splitter", SentenceSplitterAPI.getAEImport());
		//
		aeDescriptions.put("morph-analyzer", MorphologyAnnotator.createDescription(
				DefaultAnnotationAdapter.class, PosTaggerAPI.getTypeSystemDescription()));
		//
		aeDescriptions.put("tag-assembler", TagAssembler.createDescription());
		AnalysisEngineDescription desc = PipelineDescriptorUtils
				.createAggregateDescription(aeDescriptions);
		// bind the dictionary resource
		bindExternalResource(desc,
				"morph-analyzer/" + MorphologyAnnotator.RESOURCE_KEY_DICTIONARY, morphDictDesc);
		bindExternalResource(desc,
				"tag-assembler/" + GramModelBasedTagMapper.RESOURCE_GRAM_MODEL, morphDictDesc);

		FileOutputStream out = FileUtils.openOutputStream(new File(outputPath));
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
