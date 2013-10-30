/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static com.google.common.collect.Sets.newHashSet;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;

import ru.kfu.itis.cll.uima.annotator.AnnotationRemover;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPartitioningTask;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.FeatureExtractionTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.lab.task.Task;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@Parameters(separators = " =")
public class DictionaryAwareBaselineLab extends LabLauncherBase {

	private static final String KEY_MODEL_DIR = "ModelDir";
	private static final String DAB_MODEL_FILE_NAME = "dab.ser";
	private static final String SUFFIX_MODEL_FILE_NAME = "suffix.ser";

	public static void main(String[] args) throws IOException {
		/* configuration parameters:
		 * - path to corpus
		 * - folds number (for cross-validation)
		 * - PoS-categories
		 * ? output directory (for reports and model assets)
		 * - working directory (for DKPro-Lab internals) | ='wrk'
		 */
		// read configuration from command line arguments
		System.setProperty("DKPRO_HOME", "wrk");
		Slf4jLoggerImpl.forceUsingThisImplementation();
		DictionaryAwareBaselineLab lab = new DictionaryAwareBaselineLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	// the leading '_' is added to avoid confusion in Task classes
	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> _posCategoriesList;
	private Set<String> _posCategories;

	private DictionaryAwareBaselineLab() {
	}

	private void run() throws IOException {
		//
		_posCategories = newHashSet(_posCategoriesList);
		// prepare input TypeSystem
		final TypeSystemDescription inputTS = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		// prepare morph dictionary resource
		final ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				CachedSerializedDictionaryResource.class, "file:dict.opcorpora.ser");
		/*
		 * Create a pre-processing task (use whole source corpus)
		 * Aggregate AE:
		 * - PoS-trimmer with injected PoS-categories parameter
		 * - XMIWriter (to 'Corpus')
		 */
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		// -----------------------------------------------------------------
		Task corpusPartitioningTask = new CorpusPartitioningTask(foldsNum);
		/* create a training task (use 'training' FS with XmiCollectionReader on 'Corpus')
		 * Aggregate AE:
		 * - DictionaryAwareBaselineLearner (ensure thread-safety!)
		 */
		UimaTask trainingTask = new FeatureExtractionTaskBase("Training", inputTS) {
			@Discriminator
			Set<String> posCategories;

			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
				AnalysisEngineDescription dabModelLearnerDesc = createPrimitiveDescription(
						DictionaryAwareBaselineLearner.class, inputTS,
						DictionaryAwareBaselineLearner.PARAM_TARGET_POS_CATEGORIES, posCategories,
						DictionaryAwareBaselineLearner.PARAM_MODEL_OUTPUT_FILE,
						getDABModelFile(modelDir));
				AnalysisEngineDescription suffixModelTrainerDesc = createPrimitiveDescription(
						SuffixExaminingPosTrainer.class,
						SuffixExaminingPosTrainer.PARAM_WFSTORE_FILE, getSuffixModelFile(modelDir));
				try {
					bindResource(dabModelLearnerDesc,
							DictionaryAwareBaselineLearner.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
					bindResource(suffixModelTrainerDesc,
							SuffixExaminingPosTrainer.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
				} catch (InvalidXMLException e) {
					throw new IllegalStateException(e);
				}
				return createAggregateDescription(dabModelLearnerDesc, suffixModelTrainerDesc);
			}
		};
		/* Create an analysis task (use 'testing' FS with XmiCollectionReader on 'Corpus')
		 * Aggregate AE:
		 * - remove gold annotations (Word, etc)
		 * - DictionaryAwareBaselineTagger
		 * - XMIWriter
		 */
		UimaTask analysisTask = new AnalysisTaskBase("Analysis", inputTS) {

			@Discriminator
			Set<String> posCategories;

			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
				File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
				AnalysisEngineDescription goldRemoverDesc = createPrimitiveDescription(
						AnnotationRemover.class, inputTS,
						AnnotationRemover.PARAM_NAMESPACES_TO_REMOVE,
						Arrays.asList("org.opencorpora.cas"));
				AnalysisEngineDescription dabTaggerDesc = createPrimitiveDescription(
						DictionaryAwareBaselineTagger.class,
						DictionaryAwareBaselineTagger.PARAM_TARGET_POS_CATEGORIES, posCategories);
				AnalysisEngineDescription suffixTaggerDesc = createPrimitiveDescription(
						SuffixExaminingPosTagger.class,
						SuffixExaminingPosTagger.PARAM_USE_DEBUG_GRAMMEMS, true);
				// bind dictionary and wfStore resources
				ExternalResourceDescription dabWfStoreDesc = createExternalResourceDescription(
						SharedDefaultWordformStore.class,
						getDABModelFile(modelDir));
				ExternalResourceDescription suffixWfStoreDesc = createExternalResourceDescription(
						SharedDefaultWordformStore.class,
						getSuffixModelFile(modelDir));
				AnalysisEngineDescription xmiWriterDesc = createPrimitiveDescription(
						XmiWriter.class,
						XmiWriter.PARAM_OUTPUTDIR, outputDir);
				try {
					bindResource(dabTaggerDesc,
							DictionaryAwareBaselineTagger.RESOURCE_WFSTORE, dabWfStoreDesc);
					bindResource(dabTaggerDesc,
							DictionaryAwareBaselineTagger.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
					bindResource(suffixTaggerDesc,
							SuffixExaminingPosTagger.RESOURCE_WFSTORE, suffixWfStoreDesc);
					bindResource(suffixTaggerDesc,
							SuffixExaminingPosTagger.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
				} catch (InvalidXMLException e) {
					throw new ResourceInitializationException(e);
				}
				return createAggregateDescription(goldRemoverDesc, dabTaggerDesc, suffixTaggerDesc,
						xmiWriterDesc);
			}
		};
		// Create an evaluation task (use 'testing' as gold and analysis output as an evaluation target)
		Task evaluationTask = new EvaluationTask();
		/* TODO create and attach evaluation reports to:
		 * training task (save model)
		 * analysis task (save output)
		 * evaluation task (save results) 
		 */
		// configure data-flow between tasks
		corpusPartitioningTask.addImport(preprocessingTask, KEY_CORPUS);
		trainingTask.addImport(corpusPartitioningTask, KEY_CORPUS);
		analysisTask.addImport(corpusPartitioningTask, KEY_CORPUS);
		analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
		evaluationTask.addImport(corpusPartitioningTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		// -----------------------------------------------------------------
		/* create parameter space
		 * - Dimension for the source corpus
		 * - Dimension for PoS-categories
		 * - DimensionBundle for corpus-splits
		 */
		Integer[] foldValues = ContiguousSet.create(
				Range.closedOpen(0, foldsNum),
				DiscreteDomain.integers()).toArray(new Integer[0]);
		@SuppressWarnings("unchecked")
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.create(DISCRIMINATOR_SOURCE_CORPUS_DIR, srcCorpusDir),
				Dimension.create(DISCRIMINATOR_POS_CATEGORIES, _posCategories),
				Dimension.create(DISCRIMINATOR_FOLD, foldValues));
		// -----------------------------------------------------------------
		// create and run BatchTask
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(corpusPartitioningTask);
		batchTask.addTask(trainingTask);
		batchTask.addTask(analysisTask);
		batchTask.addTask(evaluationTask);

		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		try {
			Lab.getInstance().run(batchTask);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private File getDABModelFile(File modelDir) {
		return new File(modelDir, DAB_MODEL_FILE_NAME);
	}

	private File getSuffixModelFile(File modelDir) {
		return new File(modelDir, SUFFIX_MODEL_FILE_NAME);
	}
}
