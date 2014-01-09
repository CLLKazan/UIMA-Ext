/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_MODEL_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_TRAINING_DIR;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.cleartk.classifier.jar.DirectoryDataWriterFactory;
import org.cleartk.classifier.jar.JarClassifierBuilder;
import org.cleartk.classifier.jar.SequenceJarClassifierFactory;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeClassifierBuilder;
import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeDataWriterFactory;
import ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.FeatureExtractionTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.lab.task.Task;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TieredPosTaggerLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/tiered-pos-tagger";

	public static void main(String[] args) throws IOException {
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		TieredPosTaggerLab lab = new TieredPosTaggerLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	@Parameter(names = { "--pos-tiers" }, required = true)
	private List<String> _posTiers;

	private TieredPosTaggerLab() {
	}

	private void run() throws IOException {
		// create task instances
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		// -----------------------------------------------------------------
		UimaTask featureExtractionTask = new FeatureExtractionTaskBase("FeatureExtraction", inputTS) {
			@Discriminator
			int leftContextSize;
			@Discriminator
			int rightContextSize;

			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File trainingBaseDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR,
						AccessMode.READWRITE);
				List<AnalysisEngineDescription> posTaggerDescs = Lists.newArrayList();
				List<String> posTaggerNames = Lists.newArrayList();
				for (int i = 0; i < _posTiers.size(); i++) {
					String posTier = _posTiers.get(i);

					File trainingDir = getTierDir(trainingBaseDir, posTier);
					AnalysisEngineDescription ptDesc = createPrimitiveDescription(
							TieredPosSequenceAnnotator.class,
							TieredPosSequenceAnnotator.PARAM_POS_TIERS, _posTiers,
							TieredPosSequenceAnnotator.PARAM_CURRENT_TIER, i,
							TieredPosSequenceAnnotator.PARAM_LEFT_CONTEXT_SIZE, leftContextSize,
							TieredPosSequenceAnnotator.PARAM_RIGHT_CONTEXT_SIZE, rightContextSize,
							TieredPosSequenceAnnotator.PARAM_DATA_WRITER_FACTORY_CLASS_NAME,
							CRFSuiteStringOutcomeDataWriterFactory.class.getName(),
							DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
							trainingDir);
					ptDesc.getAnalysisEngineMetaData().getOperationalProperties()
							.setMultipleDeploymentAllowed(false);
					try {
						bindResource(ptDesc,
								TieredPosSequenceAnnotator.RESOURCE_KEY_MORPH_DICTIONARY,
								morphDictDesc);
					} catch (InvalidXMLException e) {
						throw new IllegalStateException(e);
					}
					posTaggerDescs.add(ptDesc);
					posTaggerNames.add(ptDesc.getImplementationName() + "-" + posTier);
				}
				return createAggregateDescription(posTaggerDescs, posTaggerNames,
						null, null, null, null);
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

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File trainingBaseDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR,
						AccessMode.READONLY);
				File modelBaseDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
				for (String posTier : _posTiers) {
					// set training parameters
					List<String> trainerArgs = Lists.newArrayList();
					trainerArgs.add("-a");
					trainerArgs.add("lbfgs");
					addTrainParam(trainerArgs, "max_iterations", 120);
					addTrainParam(trainerArgs, "feature.minfreq", featureMinFreq);
					if (featurePossibleStates) {
						addTrainParam(trainerArgs, "feature.possible_states", 1);
					}
					if (featurePossibleTransitions) {
						addTrainParam(trainerArgs, "feature.possible_transitions", 1);
					}
					addTrainParam(trainerArgs, "c2", c2);
					// ------
					File trainingDir = getTierDir(trainingBaseDir, posTier);
					File modelDir = getTierDir(modelBaseDir, posTier);
					// TODO The following lines contain a few hacks to avoid
					// extensive training file duplicates reproduction
					JarClassifierBuilder<?> _classifierBuilder = JarClassifierBuilder
							.fromTrainingDirectory(trainingDir);
					CRFSuiteStringOutcomeClassifierBuilder classifierBuilder =
							(CRFSuiteStringOutcomeClassifierBuilder) _classifierBuilder;
					// invoke implementation-specific method (i.e., it is not declared in the interface)
					classifierBuilder.trainClassifier(modelDir, trainingDir,
							trainerArgs.toArray(new String[trainerArgs.size()]));
					classifierBuilder.packageClassifier(modelDir);
				}
			}
		};
		// -----------------------------------------------------------------
		UimaTask analysisTask = new AnalysisTaskBase("Analysis", inputTS, PartitionType.DEV) {
			@Discriminator
			int leftContextSize;
			@Discriminator
			int rightContextSize;

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
				for (int i = 0; i < _posTiers.size(); i++) {
					String posTier = _posTiers.get(i);

					File modelDir = getTierDir(modelBaseDir, posTier);
					AnalysisEngineDescription ptDesc = createPrimitiveDescription(
							TieredPosSequenceAnnotator.class,
							TieredPosSequenceAnnotator.PARAM_POS_TIERS, _posTiers,
							TieredPosSequenceAnnotator.PARAM_CURRENT_TIER, i,
							TieredPosSequenceAnnotator.PARAM_LEFT_CONTEXT_SIZE, leftContextSize,
							TieredPosSequenceAnnotator.PARAM_RIGHT_CONTEXT_SIZE, rightContextSize,
							TieredPosSequenceAnnotator.PARAM_CLASSIFIER_FACTORY_CLASS_NAME,
							SequenceJarClassifierFactory.class.getName(),
							SequenceJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
							JarClassifierBuilder.getModelJarFile(modelDir));
					try {
						bindResource(ptDesc,
								TieredPosSequenceAnnotator.RESOURCE_KEY_MORPH_DICTIONARY,
								morphDictDesc);
					} catch (InvalidXMLException e) {
						throw new IllegalStateException(e);
					}
					primitiveDescs.add(ptDesc);
					primitiveNames.add(ptDesc.getImplementationName() + "-" + posTier);
				}
				//
				AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
				primitiveDescs.add(xmiWriterDesc);
				primitiveNames.add("xmiWriter");
				//
				return createAggregateDescription(primitiveDescs, primitiveNames,
						null, null, null, null);
			}
		};
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
		// TODO
		/*Integer[] foldValues = ContiguousSet.create(
				Range.closedOpen(0, foldsNum),
				DiscreteDomain.integers()).toArray(new Integer[0]);*/
		Set<String> posCategories = Sets.newLinkedHashSet();
		for (String pt : _posTiers) {
			posCategories.addAll(Lists.newLinkedList(posCatSplitter.split(pt)));
		}
		@SuppressWarnings("unchecked")
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.create(DISCRIMINATOR_SOURCE_CORPUS_DIR, srcCorpusDir),
				Dimension.create(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR, corpusSplitDir),
				// posCategories discriminator is used in the preprocessing task
				Dimension.create(DISCRIMINATOR_POS_CATEGORIES, posCategories),
				Dimension.create(DISCRIMINATOR_FOLD, 0),
				// Dimension.create("featureMinFreq", 1, 4, 9, 19),
				Dimension.create("featureMinFreq", 0),
				// Dimension.create("c2", 1, 10),
				Dimension.create("c2", 1),
				// Dimension.create("featurePossibleTransitions", false, true),
				Dimension.create("featurePossibleTransitions", true),
				// Dimension.create("featurePossibleStates", false, true));
				Dimension.create("featurePossibleStates", true),
				Dimension.create("leftContextSize", 1, 2, 3),
				Dimension.create("rightContextSize", 1, 2));
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

	private File getTierDir(File baseDir, String posTier) {
		// TODO escape chars that are not safe for filename
		posTier = posTier.replace("&", "_and_");
		return new File(baseDir, posTier);
	}

	private void addTrainParam(List<String> params, String name, int value) {
		params.add("-p");
		params.add(name + "=" + value);
	}

	private static Splitter posCatSplitter = Splitter.onPattern("[,&]");
}
