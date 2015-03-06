/**
 * 
 */
package ru.kfu.itis.issst.uima.depparser.lab;

import static ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils.createOverrideParameterDeclaration;

import java.io.File;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.fit.factory.ConfigurationParameterFactory;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.depparser.mst.MSTCollectionReader;
import ru.kfu.itis.issst.uima.depparser.mst.MSTWriter;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;

/**
 * Re-assign pos-tags for given input dataset in MST format.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AssignPosTags {

	public static void main(String[] args) throws Exception {
		AssignPosTags obj = new AssignPosTags();
		JCommander com = new JCommander(obj);
		try {
			com.parse(args);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			com.usage();
			System.exit(1);
		}
		obj.run();
	}

	@Parameter(names = { "-i", "--input-file" }, required = true)
	private File inputFile;
	@Parameter(names = { "-o", "--output-file" }, required = true)
	private File outputFile;

	private AssignPosTags() {
	}

	private void run() throws Exception {
		CpeBuilder cpeBuilder = new CpeBuilder();
		//
		cpeBuilder.setReader(MSTCollectionReader.createDescription(inputFile));
		//
		List<MetaDataObject> aeDescs = Lists.newLinkedList();
		List<String> aeNames = Lists.newLinkedList();
		//
		aeDescs.add(PosTaggerAPI.getAEImport());
		aeNames.add("pos-tagger");
		//
		aeDescs.add(MSTWriter.createDescription(outputFile));
		aeNames.add("mstWriter");
		//
		AnalysisEngineDescription pipeDesc =
				PipelineDescriptorUtils.createAggregateDescription(aeDescs, aeNames);
		// set reuseExistingWords = true in pos-tagger
		ConfigurationParameter rewParam =
				PosTaggerAPI.createReuseExistingWordAnnotationParameterDeclaration();
		createOverrideParameterDeclaration(rewParam, pipeDesc,
				"pos-tagger", PosTaggerAPI.PARAM_REUSE_EXISTING_WORD_ANNOTATIONS);
		ConfigurationParameterFactory.setParameter(pipeDesc, rewParam.getName(), true);
		//
		cpeBuilder.addAnalysisEngine(pipeDesc);
		//
		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe, 50));
		cpe.process();
	}

	static {
		Slf4jLoggerImpl.forceUsingThisImplementation();
	}
}
