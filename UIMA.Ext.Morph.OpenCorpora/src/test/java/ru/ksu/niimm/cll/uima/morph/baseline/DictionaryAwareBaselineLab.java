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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.annotator.AnnotationRemover;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.cpe.XmiFileListReader;
import ru.kfu.itis.cll.uima.eval.EvaluationLauncher;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;
import ru.kfu.itis.cll.uima.util.CorpusSplit;
import ru.kfu.itis.cll.uima.util.CorpusUtils;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmingAnnotator;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Maps;
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
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.impl.UimaTaskBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@Parameters(separators = " =")
public class DictionaryAwareBaselineLab {

	private static final String KEY_CORPUS = "Corpus";
	private static final String KEY_MODEL_DIR = "ModelDir";
	private static final String KEY_OUTPUT_DIR = "OutputDir";
	private static final String PLACEHOLDER_OUTPUT_BASE_DIR = "outputBaseDir";
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

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Parameter(names = { "-f", "--folds" }, required = true, description = "Number of cross-validation folds")
	private int _foldsNum;
	// the leading '_' is added to avoid confusion in Task classes
	@Parameter(names = { "-c", "--corpus" }, required = true, description = "Path to corpus directory")
	private File _srcCorpusDir;
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
		UimaTask preprocessingTask = new UimaTaskBase() {
			{
				setType("CorpusPreProcessing");
			}
			@Discriminator
			Set<String> posCategories;
			@Discriminator
			File srcCorpusDir;

			@Override
			public CollectionReaderDescription getCollectionReaderDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				return CollectionReaderFactory.createDescription(XmiCollectionReader.class,
						inputTS,
						XmiCollectionReader.PARAM_INPUTDIR, srcCorpusDir.getPath());
			}

			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				AnalysisEngineDescription posTrimmerDesc = createPrimitiveDescription(
						PosTrimmingAnnotator.class, inputTS,
						PosTrimmingAnnotator.PARAM_TARGET_POS_CATEGORIES, posCategories);
				try {
					bindResource(posTrimmerDesc, PosTrimmingAnnotator.RESOURCE_MORPH_DICTIONARY,
							morphDictDesc);
				} catch (InvalidXMLException e) {
					throw new ResourceInitializationException(e);
				}
				AnalysisEngineDescription xmiWriterDesc = createPrimitiveDescription(
						XmiWriter.class,
						XmiWriter.PARAM_OUTPUTDIR,
						taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READWRITE));
				return createAggregateDescription(posTrimmerDesc, xmiWriterDesc);
			}
		};
		// -----------------------------------------------------------------
		Task corpusPartitioningTask = new ExecutableTaskBase() {
			{
				setType("CorpusPartioning");
			}
			@Discriminator
			File srcCorpusDir;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.ADD_ONLY);
				List<CorpusSplit> corpusSplits = CorpusUtils.createCrossValidationSplits(corpusDir,
						FileFilterUtils.suffixFileFilter(".xmi"), _foldsNum);
				for (int i = 0; i < corpusSplits.size(); i++) {
					writeFileLists(corpusDir, i, corpusSplits.get(i));
				}
			}
		};
		/* create a training task (use 'training' FS with XmiCollectionReader on 'Corpus')
		 * Aggregate AE:
		 * - DictionaryAwareBaselineLearner (ensure thread-safety!)
		 */
		UimaTask trainingTask = new UimaTaskBase() {
			{
				setType("Training");
			}
			@Discriminator
			int fold;
			@Discriminator
			Set<String> posCategories;

			@Override
			public CollectionReaderDescription getCollectionReaderDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
				File trainingListFile = getTrainingListFile(corpusDir, fold);
				return CollectionReaderFactory.createDescription(XmiFileListReader.class, inputTS,
						XmiFileListReader.PARAM_BASE_DIR, corpusDir.getPath(),
						XmiFileListReader.PARAM_LIST_FILE, trainingListFile.getPath());
			}

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
				// this is workaround for https://issues.apache.org/jira/browse/UIMA-2798
				// remove after migration to UIMAfit 2.0.x
				return createAggregateDescription(dabModelLearnerDesc, suffixModelTrainerDesc);
			}
		};
		/* Create an analysis task (use 'testing' FS with XmiCollectionReader on 'Corpus')
		 * Aggregate AE:
		 * - remove gold annotations (Word, etc)
		 * - DictionaryAwareBaselineTagger
		 * - XMIWriter
		 */
		UimaTask analysisTask = new UimaTaskBase() {
			{
				setType("Analysis");
			}
			@Discriminator
			int fold;
			@Discriminator
			Set<String> posCategories;

			@Override
			public CollectionReaderDescription getCollectionReaderDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
				File testingListFile = getTestingListFile(corpusDir, fold);
				return CollectionReaderFactory.createDescription(XmiFileListReader.class, inputTS,
						XmiFileListReader.PARAM_BASE_DIR, corpusDir.getPath(),
						XmiFileListReader.PARAM_LIST_FILE, testingListFile.getPath());
			}

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
		Task evaluationTask = new ExecutableTaskBase() {
			{
				setType("Evaluation");
			}
			@Discriminator
			int fold;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.ADD_ONLY);
				File goldDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
				Properties evalCfg = readEvaluationConfig();
				// replace placeholders
				Map<String, String> phValues = Maps.newHashMap();
				phValues.put(PLACEHOLDER_OUTPUT_BASE_DIR, outputDir.getPath());
				ConfigPropertiesUtils.replacePlaceholders(evalCfg, phValues);
				evalCfg.setProperty("goldCasDirectory.dir", goldDir.getPath());
				evalCfg.setProperty("goldCasDirectory.listFile",
						getTestingListFile(goldDir, fold).getPath());
				evalCfg.setProperty("systemCasDirectory.dir", outputDir.getPath());
				if (log.isInfoEnabled()) {
					log.info("Evaluation config:\n {}",
							ConfigPropertiesUtils.prettyString(evalCfg));
				}
				EvaluationLauncher.runUsingProperties(evalCfg);
			}
		};
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
				Range.closedOpen(0, _foldsNum),
				DiscreteDomain.integers()).toArray(new Integer[0]);
		@SuppressWarnings("unchecked")
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.create("srcCorpusDir", _srcCorpusDir),
				Dimension.create("posCategories", _posCategories),
				Dimension.create("fold", foldValues));
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

	private static final String TRAINING_LIST_SUFFIX = "training-";
	private static final String TESTING_LIST_SUFFIX = "testing-";

	private void writeFileLists(File outputDir, int i, CorpusSplit corpusSplit)
			throws IOException {
		File trainingList = getTrainingListFile(outputDir, i);
		File testingList = getTestingListFile(outputDir, i);
		FileUtils.writeLines(trainingList, "utf-8", corpusSplit.getTrainingSetPaths());
		FileUtils.writeLines(testingList, "utf-8", corpusSplit.getTestingSetPaths());
	}

	private File getTrainingListFile(File dir, int fold) {
		return new File(dir, TRAINING_LIST_SUFFIX + fold + ".list");
	}

	private File getTestingListFile(File dir, int fold) {
		return new File(dir, TESTING_LIST_SUFFIX + fold + ".list");
	}

	private File getDABModelFile(File modelDir) {
		return new File(modelDir, DAB_MODEL_FILE_NAME);
	}

	private File getSuffixModelFile(File modelDir) {
		return new File(modelDir, SUFFIX_MODEL_FILE_NAME);
	}

	private Properties readEvaluationConfig() throws IOException {
		Properties evalProps = new Properties();
		String evalPropsPath = "baseline-eval.properties";
		InputStream evalPropsIS = getClassLoader().getResourceAsStream(evalPropsPath);
		if (evalPropsIS == null) {
			throw new IllegalStateException(String.format("Can't find classpath resource %s",
					evalPropsPath));
		}
		Reader evalPropsReader = new BufferedReader(new InputStreamReader(evalPropsIS, "utf-8"));
		try {
			evalProps.load(evalPropsReader);
		} finally {
			evalPropsReader.close();
		}
		return evalProps;
	}

	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}