/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import com.beust.jcommander.JCommander;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.*;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.ConfigurationParameterFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.cleartk.GenericJarClassifierFactory;
import ru.ksu.niimm.cll.uima.morph.lab.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils.getResourceManagerConfiguration;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.*;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TieredPosTaggerLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/tiered-pos-tagger";

	public static void main(String[] args) throws IOException {
		System.setProperty("DKPRO_HOME", new File(DEFAULT_WRK_DIR).getAbsolutePath());
		TieredPosTaggerLab lab = new TieredPosTaggerLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	private TieredPosTaggerLab() {
	}

	private void run() throws IOException {
		// create task instances
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, gramModelDesc);
		// -----------------------------------------------------------------
		UimaTask featureExtractionTask = new FeatureExtractionTaskBase("FeatureExtraction", inputTS) {
			@Discriminator
			List<String> posTiers;
			@Discriminator
			int leftContextSize;
			@Discriminator
			int rightContextSize;
			@Discriminator
			boolean generateDictionaryFeatures;

			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File trainingBaseDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR,
						AccessMode.READWRITE);
				Map<String, Object> taggerParams = Maps.newHashMap();
				taggerParams.put(TieredPosSequenceAnnotator.PARAM_LEFT_CONTEXT_SIZE,
						leftContextSize);
				taggerParams.put(TieredPosSequenceAnnotator.PARAM_RIGHT_CONTEXT_SIZE,
						rightContextSize);
				taggerParams.put(TieredPosSequenceAnnotator.PARAM_GEN_DICTIONARY_FEATURES,
						generateDictionaryFeatures);
				AnalysisEngineDescription trDataWriterDesc = TieredPosSequenceAnnotatorFactory
						.getTrainingDataWriterDescriptor(posTiers, taggerParams, trainingBaseDir);
				// prepare aggregate with required resources
				AnalysisEngineDescription aggrDesc = createEngineDescription(
                        Arrays.asList(trDataWriterDesc),
                        Arrays.asList("trainin-data-writer"),
                        null, null, null);
				// name of the dictionary resource is already set in LabLauncherBase
				getResourceManagerConfiguration(aggrDesc).addExternalResource(morphDictDesc);
				// wrap it into another aggregate to avoid wrapping of delegates into separate
				// CPEIntegrateCasProcessors by org.uimafit.factory.CpeBuilder
				return createEngineDescription(aggrDesc);
			}
		};
		// -----------------------------------------------------------------
		Task trainingTask = new ExecutableTaskBase() {
			{
				setType("Training");
			}
			@Discriminator
			int featureMinFreq;
			@Discriminator
			boolean featurePossibleStates;
			@Discriminator
			boolean featurePossibleTransitions;
			@Discriminator
			int c2;
			@Discriminator
			int optMaxIterations;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File trainingBaseDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR,
						AccessMode.READONLY);
				File modelBaseDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
				//
				// set training parameters
				List<String> trainerArgs = Lists.newArrayList();
				trainerArgs.add("-a");
				trainerArgs.add("lbfgs");
				addTrainParam(trainerArgs, "max_iterations", optMaxIterations);
				addTrainParam(trainerArgs, "feature.minfreq", featureMinFreq);
				if (featurePossibleStates) {
					addTrainParam(trainerArgs, "feature.possible_states", 1);
				}
				if (featurePossibleTransitions) {
					addTrainParam(trainerArgs, "feature.possible_transitions", 1);
				}
				addTrainParam(trainerArgs, "c2", c2);
				//
				TieredPosSequenceAnnotatorFactory.trainModels(trainingBaseDir, modelBaseDir,
						trainerArgs.toArray(new String[trainerArgs.size()]));
			}
		};
		// -----------------------------------------------------------------
		UimaTask analysisTask = new AnalysisTask(inputTS, morphDictDesc, PartitionType.DEV);
		// -----------------------------------------------------------------
		Task evaluationTask = new EvaluationTask(PartitionType.DEV);
		// -----------------------------------------------------------------
		// configure data-flow between tasks
		featureExtractionTask.addImport(preprocessingTask, KEY_CORPUS);
		trainingTask.addImport(featureExtractionTask, KEY_TRAINING_DIR);
		analysisTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
		evaluationTask.addImport(preprocessingTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		// -----------------------------------------------------------------
		// create parameter space
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension(DISCRIMINATOR_SOURCE_CORPUS_DIR),
				getFileDimension(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR),
				// posCategories discriminator is used in the preprocessing task
				getStringSetDimension(DISCRIMINATOR_POS_CATEGORIES),
				getStringListDimension(DISCRIMINATOR_POS_TIERS),
				Dimension.create(DISCRIMINATOR_FOLD, 0),
				// Dimension.create("featureMinFreq", 1, 4, 9, 19),
				getIntDimension("featureMinFreq"),
				// Dimension.create("c2", 1, 10),
				getIntDimension("c2"),
				// Dimension.create("featurePossibleTransitions", false, true),
				getBoolDimension("featurePossibleTransitions"),
				// Dimension.create("featurePossibleStates", false, true));
				getBoolDimension("featurePossibleStates"),
				getIntDimension("optMaxIterations"),
				getIntDimension("leftContextSize"),
				getIntDimension("rightContextSize"),
				getBoolDimension("generateDictionaryFeatures"));
		pSpace.addConstraint(new Constraint() {
			@SuppressWarnings("unchecked")
			@Override
			public boolean isValid(Map<String, Object> cfg) {
				List<String> posTiers = (List<String>) cfg.get(DISCRIMINATOR_POS_TIERS);
				Set<String> expectedPosCats = getAllCategories(posTiers);
				Set<String> actualPosCats = (Set<String>) cfg.get(DISCRIMINATOR_POS_CATEGORIES);
				return expectedPosCats.equals(actualPosCats);
			}
		});
		// -----------------------------------------------------------------
		// create and run BatchTask
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(featureExtractionTask);
		batchTask.addTask(trainingTask);
		batchTask.addTask(analysisTask);
		batchTask.addTask(evaluationTask);
		// 
		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		try {
			Lab.getInstance().run(batchTask);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private void addTrainParam(List<String> params, String name, int value) {
		params.add("-p");
		params.add(name + "=" + value);
	}

	private static Splitter posCatSplitter = Splitter.onPattern("[,&]");

	static Set<String> getAllCategories(List<String> posTiers) {
		Set<String> posCategories = Sets.newLinkedHashSet();
		for (String pt : posTiers) {
			posCategories.addAll(Lists.newLinkedList(posCatSplitter.split(pt)));
		}
		return posCategories;
	}

	static class AnalysisTask extends AnalysisTaskBase {

		private ExternalResourceDescription morphDictDesc;

		AnalysisTask(TypeSystemDescription inputTS,
				ExternalResourceDescription morphDictDesc,
				PartitionType targetPartition) {
			super(PartitionType.DEV.equals(targetPartition) ? "Analysis" : "AnalysisFinal",
					inputTS, targetPartition);
			this.morphDictDesc = morphDictDesc;
		}

		@Override
		public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
				throws ResourceInitializationException, IOException {
			File modelBaseDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
			File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
			// 
			List<AnalysisEngineDescription> primitiveDescs = Lists.newArrayList();
			List<String> primitiveNames = Lists.newArrayList();
			//
			AnalysisEngineDescription goldRemoverDesc = createGoldRemoverDesc();
			primitiveDescs.add(goldRemoverDesc);
			primitiveNames.add("goldRemover");
			//
			AnalysisEngineDescription taggerDesc = TieredPosSequenceAnnotatorFactory
					.createTaggerDescription(modelBaseDir);
			primitiveDescs.add(taggerDesc);
			primitiveNames.add("pos-tagger");
			// We should specify additional paths to resolve relative paths of model jars.  
			// There are several ways to do this. E.g., we can change global UIMA data-path.
			// But the better solution is to provide the parameter for JarClassifierFactory.
			ConfigurationParameterFactory.setParameter(taggerDesc,
					GenericJarClassifierFactory.PARAM_ADDITIONAL_SEARCH_PATHS,
					new String[] { modelBaseDir.getPath() });
			// TODO:LOW
			// disable multiple deployment to avoid heavy memory consumption and related consequences 
			taggerDesc.getAnalysisEngineMetaData().getOperationalProperties()
					.setMultipleDeploymentAllowed(false);
			//
			AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
			primitiveDescs.add(xmiWriterDesc);
			primitiveNames.add("xmiWriter");
			//
			AnalysisEngineDescription aggrDesc = createEngineDescription(
                    primitiveDescs, primitiveNames,
                    null, null, null);
			// add MorphDictionaryHolder resource with the required name
			getResourceManagerConfiguration(aggrDesc).addExternalResource(morphDictDesc);
			// wrap it into another aggregate to avoid wrapping of delegates into separate
			// CPEIntegrateCasProcessors by org.uimafit.factory.CpeBuilder
			return createEngineDescription(aggrDesc);
		}
	}

	private static final String DISCRIMINATOR_POS_TIERS = "posTiers";
}
