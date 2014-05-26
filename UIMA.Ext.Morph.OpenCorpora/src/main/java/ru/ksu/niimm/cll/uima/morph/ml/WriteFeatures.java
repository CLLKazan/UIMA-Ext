/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class WriteFeatures {

	public static void main(String[] args) throws Exception {
		WriteFeatures obj = new WriteFeatures();
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

	// input - training set
	@Parameter(names = "--training-xmi-dir", required = true)
	private File trainingSetXmiDir;
	// config - define required gram categories
	@Parameter(names = "--pos-tiers", required = true)
	private List<String> posTiers;
	// output
	@Parameter(names = "--output-dir", required = true)
	private File outputBaseDir;
	// optional parameters
	@Parameter(names = "--left-ctx")
	private int leftContextSize = 2;
	@Parameter(names = "--right-ctx")
	private int rightContextSize = 1;

	private WriteFeatures() {
	}

	private void run() throws Exception {
		CpeBuilder cpeBuilder = new CpeBuilder();
		// setup TypeSystem
		TypeSystemDescription inputTS = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				TokenizerAPI.TYPESYSTEM_TOKENIZER,
				"ru.kfu.itis.issst.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		// setup a training set collection reader
		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createDescription(
				XmiCollectionReader.class, inputTS,
				XmiCollectionReader.PARAM_INPUTDIR, trainingSetXmiDir.getPath());
		cpeBuilder.setReader(colReaderDesc);
		// setup a morph dictionary
		ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				CachedSerializedDictionaryResource.class,
				"file:dict.opcorpora.ser");
		// setup a pipeline extracting features
		List<AnalysisEngineDescription> taggerDescs = Lists.newArrayList();
		List<String> taggerNames = Lists.newArrayList();
		Map<String, Object> taggerParams = Maps.newHashMap();
		taggerParams.put(TieredPosSequenceAnnotator.PARAM_LEFT_CONTEXT_SIZE,
				leftContextSize);
		taggerParams.put(TieredPosSequenceAnnotator.PARAM_RIGHT_CONTEXT_SIZE,
				rightContextSize);
		TieredPosSequenceAnnotatorFactory.addTrainingDataWriterDescriptors(
				posTiers, taggerParams,
				outputBaseDir, morphDictDesc, taggerDescs, taggerNames);
		AnalysisEngineDescription pipelineDesc = createAggregateDescription(
				taggerDescs, taggerNames, null, null, null, null);
		cpeBuilder.addAnalysisEngine(pipelineDesc);
		//
		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe, 50));
		cpe.process();
	}

	static {
		Slf4jLoggerImpl.forceUsingThisImplementation();
	}
}
