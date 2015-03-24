/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils.getResourceManagerConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
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
				SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
				PosTaggerAPI.TYPESYSTEM_POSTAGGER);
		// setup a training set collection reader
		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createReaderDescription(
				XmiCollectionReader.class, inputTS,
				XmiCollectionReader.PARAM_INPUTDIR, trainingSetXmiDir.getPath());
		cpeBuilder.setReader(colReaderDesc);
		// setup a morph dictionary
		ExternalResourceDescription morphDictDesc = MorphDictionaryAPIFactory
				.getMorphDictionaryAPI()
				.getResourceDescriptionForCachedInstance();
		// setup training data writer
		Map<String, Object> taggerParams = Maps.newHashMap();
		taggerParams.put(TieredPosSequenceAnnotator.PARAM_LEFT_CONTEXT_SIZE,
				leftContextSize);
		taggerParams.put(TieredPosSequenceAnnotator.PARAM_RIGHT_CONTEXT_SIZE,
				rightContextSize);
		AnalysisEngineDescription trainDataWriterDesc = TieredPosSequenceAnnotatorFactory
				.getTrainingDataWriterDescriptor(
						posTiers, taggerParams, outputBaseDir);
		// setup a pipeline extracting features
		AnalysisEngineDescription pipelineDesc = createEngineDescription(
				Arrays.asList(trainDataWriterDesc),
				Arrays.asList("training-data-writer"),
				null, null, null);
		// add required MorphDictionaryHolder resource with the specified name
		morphDictDesc.setName(PosTaggerAPI.MORPH_DICTIONARY_RESOURCE_NAME);
		getResourceManagerConfiguration(pipelineDesc).addExternalResource(morphDictDesc);
		//
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
