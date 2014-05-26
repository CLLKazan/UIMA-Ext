/**
 * 
 */
package ru.kfu.itis.issst.uima.segmentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import com.google.common.collect.Maps;

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
		Map<String, MetaDataObject> aeDescriptions = Maps.newLinkedHashMap();
		aeDescriptions.put("tokenizer", TokenizerAPI.getAEImport());

		aeDescriptions.put("sentenceSplitter", SentenceSplitterAPI.getAEImport());

		String outputPath = "desc/basic-aggregate.xml";
		AnalysisEngineDescription desc = PipelineDescriptorUtils
				.createAggregateDescription(aeDescriptions);
		FileOutputStream out = FileUtils.openOutputStream(new File(outputPath));
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
