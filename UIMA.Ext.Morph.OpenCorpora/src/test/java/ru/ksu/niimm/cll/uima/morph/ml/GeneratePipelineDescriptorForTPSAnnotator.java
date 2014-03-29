/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.uimafit.factory.ResourceCreationSpecifierFactory.createResourceCreationSpecifier;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.Constants;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.impl.FixedFlow_impl;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.xml.sax.SAXException;

import ru.kfu.cll.uima.segmentation.SentenceSplitter;
import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
import ru.kfu.cll.uima.tokenizer.PostTokenizer;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GeneratePipelineDescriptorForTPSAnnotator {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		if (args.length != 1) {
			System.err.println("Provide output path!");
			System.exit(1);
		}
		File outFile = new File(args[0]);
		//
		Map<String, MetaDataObject> aeDescriptions = Maps.newLinkedHashMap();
		aeDescriptions.put("tokenizer", InitialTokenizer.createDescription());
		aeDescriptions.put("post-tokenizer", PostTokenizer.createDescription());
		aeDescriptions.put("sentenceSplitter", SentenceSplitter.createDescription());
		Import posTaggerDescImport = new Import_impl();
		posTaggerDescImport.setName("pos_tagger");
		aeDescriptions.put("pos-tagger", posTaggerDescImport);
		//
		AnalysisEngineDescription outDesc = createAggregateDescription(
				ImmutableList.copyOf(aeDescriptions.values()),
				ImmutableList.copyOf(aeDescriptions.keySet()));
		OutputStream out = FileUtils.openOutputStream(outFile);
		try {
			outDesc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	// TODO move to utils package
	private static AnalysisEngineDescription createAggregateDescription(
			List<MetaDataObject> analysisEngineDescriptions,
			List<String> componentNames)
			throws UIMAException, IOException {

		// create the descriptor and set configuration parameters
		AnalysisEngineDescription desc = new AnalysisEngineDescription_impl();
		desc.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
		desc.setPrimitive(false);

		// if any of the aggregated analysis engines does not allow multiple
		// deployment, then the aggregate engine may also not be multiply deployed
		boolean allowMultipleDeploy = true;
		for (MetaDataObject mdo : analysisEngineDescriptions) {
			AnalysisEngineDescription d;
			if (mdo instanceof AnalysisEngineDescription) {
				d = (AnalysisEngineDescription) mdo;
			} else {
				Import aedImport = (Import) mdo;
				URL aedUrl = aedImport.findAbsoluteUrl(UIMAFramework.newDefaultResourceManager());
				d = (AnalysisEngineDescription) createResourceCreationSpecifier(aedUrl, null);
			}
			allowMultipleDeploy &= d.getAnalysisEngineMetaData().getOperationalProperties()
					.isMultipleDeploymentAllowed();
		}
		desc.getAnalysisEngineMetaData().getOperationalProperties()
				.setMultipleDeploymentAllowed(allowMultipleDeploy);

		List<String> flowNames = new ArrayList<String>();

		for (int i = 0; i < analysisEngineDescriptions.size(); i++) {
			MetaDataObject aed = analysisEngineDescriptions.get(i);
			String componentName = componentNames.get(i);
			desc.getDelegateAnalysisEngineSpecifiersWithImports().put(componentName, aed);
			flowNames.add(componentName);
		}

		FixedFlow fixedFlow = new FixedFlow_impl();
		fixedFlow.setFixedFlow(flowNames.toArray(new String[flowNames.size()]));
		desc.getAnalysisEngineMetaData().setFlowConstraints(fixedFlow);

		return desc;
	}
}
