/**
 * 
 */
package ru.kfu.itis.issst.uima.depparser.mst;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GeneratePipelineDescriptorForDepParsing {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		if (args.length != 1) {
			System.err.println("Provide output path!");
			System.exit(1);
		}
		File outFile = new File(args[0]);
		//
		Map<String, MetaDataObject> aeDescriptions = Maps.newLinkedHashMap();
		aeDescriptions.put("tokenizer", TokenizerAPI.getAEImport());
		//
		aeDescriptions.put("sentenceSplitter", SentenceSplitterAPI.getAEImport());
		//
		Import posTaggerDescImport = new Import_impl();
		posTaggerDescImport.setName("pos_tagger");
		aeDescriptions.put("pos-tagger", posTaggerDescImport);
		//
		Import depParserDescImport = new Import_impl();
		depParserDescImport.setName("dep_parser");
		aeDescriptions.put("dep-parser", depParserDescImport);
		// 
		AnalysisEngineDescription outDesc = PipelineDescriptorUtils.createAggregateDescription(
				ImmutableList.copyOf(aeDescriptions.values()),
				ImmutableList.copyOf(aeDescriptions.keySet()));
		OutputStream out = FileUtils.openOutputStream(outFile);
		try {
			outDesc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
