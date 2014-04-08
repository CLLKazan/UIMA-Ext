/**
 * 
 */
package ru.kfu.itis.issst.uima.depparser.lab;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.resource.ExternalResourceDescription;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.depparser.mst.MSTCollectionReader;
import ru.kfu.itis.issst.uima.depparser.mst.MSTWriter;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.ksu.niimm.cll.uima.morph.ml.TieredPosSequenceAnnotatorFactory;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

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
	/* TODO
	 * Make this and other launchers independent of specific PoS-tagger implementation.
	 * Pass a path (or FQN) of PoS-tagger AE as a parameter.
	 * Use UIMAFit ResoruceCreationSpecifierFactory to produce descripr object
	 */
	@Parameter(names = "--tcrf-tagger-model-dir", required = true)
	private File tcrfTaggerModelBaseDir;
	@Parameter(names = { "-o", "--output-file" }, required = true)
	private File outputFile;

	private AssignPosTags() {
	}

	private void run() throws Exception {
		CpeBuilder cpeBuilder = new CpeBuilder();
		//
		cpeBuilder.setReader(MSTCollectionReader.createDescription(inputFile));
		//
		List<AnalysisEngineDescription> aeDescs = Lists.newLinkedList();
		List<String> aeNames = Lists.newLinkedList();
		ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				CachedSerializedDictionaryResource.class,
				"file:dict.opcorpora.ser");
		TieredPosSequenceAnnotatorFactory.addTaggerDescriptions(
				tcrfTaggerModelBaseDir, true, morphDictDesc, aeDescs, aeNames);
		//
		aeDescs.add(TagAssembler.createDescription(morphDictDesc));
		aeNames.add("tagAssembler");
		//
		aeDescs.add(MSTWriter.createDescription(outputFile));
		aeNames.add("mstWriter");
		//
		AnalysisEngineDescription pipeDesc = createAggregateDescription(aeDescs, aeNames,
				null, null, null, null);
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
