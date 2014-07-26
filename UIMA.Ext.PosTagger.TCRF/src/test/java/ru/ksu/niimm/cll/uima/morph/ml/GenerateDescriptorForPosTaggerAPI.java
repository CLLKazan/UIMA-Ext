/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateDescriptorForPosTaggerAPI {

	public static void main(String[] args) throws IOException, UIMAException, SAXException {
		String outPath = "src/main/resources/"
				+ PosTaggerAPI.AE_POSTAGGER.replace('.', '/')
				+ ".xml";
		//
		Import tcrfPosTaggerImport = new Import_impl();
		// assumption: the model dir must be in UIMA datapath
		tcrfPosTaggerImport.setName(TieredPosSequenceAnnotatorFactory.POS_TAGGER_DESCRIPTOR_NAME);
		//
		AnalysisEngineDescription resultDesc = PipelineDescriptorUtils.createAggregateDescription(
				Arrays.<MetaDataObject> asList(tcrfPosTaggerImport),
				Arrays.asList("pos-tagger"));
		// add parameters specified by PosTaggerAPI
		ConfigurationParameter rewParam = PosTaggerAPI
				.createReuseExistingWordAnnotationParameterDeclaration();
		PipelineDescriptorUtils.createOverrideParameterDeclaration(rewParam, resultDesc,
				"pos-tagger",
				PosTaggerAPI.PARAM_REUSE_EXISTING_WORD_ANNOTATIONS);
		// write to an XML file
		FileOutputStream out = FileUtils.openOutputStream(new File(outPath));
		try {
			// preserve imports = true
			resultDesc.toXML(out, true);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
