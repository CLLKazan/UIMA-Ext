/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_MODEL_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;

import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.FeatureExtractionTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

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

	static final String DEFAULT_WRK_DIR = "wrk/dict-baseline";
	private static final String DAB_MODEL_FILE_NAME = "dab.ser";

	// private static final String SUFFIX_MODEL_FILE_NAME = "suffix.ser";

	public static void main(String[] args) throws IOException {
		/* configuration parameters:
		 * - path to corpus
		 * - path to files describing the corpus partitioning 
		 * - PoS-categories
		 */
		// read configuration from command line arguments
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		DictionaryAwareBaselineLab lab = new DictionaryAwareBaselineLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	private DictionaryAwareBaselineLab() {
	}

	private void run() throws IOException {
		/*
		 * Create a pre-processing task (use whole source corpus)
		 * Aggregate AE:
		 * - PoS-trimmer with injected PoS-categories parameter
		 * - XMIWriter (to 'Corpus')
		 */
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, gramModelDesc);
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
				/*
				AnalysisEngineDescription suffixModelTrainerDesc = createPrimitiveDescription(
						SuffixExaminingPosTrainer.class,
						SuffixExaminingPosTrainer.PARAM_WFSTORE_FILE, getSuffixModelFile(modelDir));
						*/
				try {
					bindResource(dabModelLearnerDesc,
							DictionaryAwareBaselineLearner.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
					/*
					bindResource(suffixModelTrainerDesc,
							SuffixExaminingPosTrainer.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
							*/
				} catch (InvalidXMLException e) {
					throw new IllegalStateException(e);
				}
				return createAggregateDescription(dabModelLearnerDesc /*, suffixModelTrainerDesc*/);
			}
		};
		/* Create an analysis task (use 'testing' FS with XmiCollectionReader on 'Corpus')
		 * Aggregate AE:
		 * - remove gold annotations (Word, etc)
		 * - DictionaryAwareBaselineTagger
		 * - XMIWriter
		 */
		UimaTask analysisTask = new AnalysisTask(PartitionType.DEV, inputTS, morphDictDesc);
		// Create an evaluation task (use 'testing' as gold and analysis output as an evaluation target)
		Task evaluationTask = new EvaluationTask(PartitionType.DEV);
		// configure data-flow between tasks
		trainingTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
		evaluationTask.addImport(preprocessingTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		// -----------------------------------------------------------------
		/* create parameter space
		 * - Dimension for the source corpus
		 * - Dimension for PoS-categories
		 * - DimensionBundle for corpus-splits
		 */
		/*Integer[] foldValues = ContiguousSet.create(
				Range.closedOpen(0, foldsNum),
				DiscreteDomain.integers()).toArray(new Integer[0]);*/
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension(DISCRIMINATOR_SOURCE_CORPUS_DIR),
				getFileDimension(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR),
				getStringSetDimension(DISCRIMINATOR_POS_CATEGORIES),
				Dimension.create(DISCRIMINATOR_FOLD, 0));
		// -----------------------------------------------------------------
		// create and run BatchTask
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
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

	private static File getDABModelFile(File modelDir) {
		return new File(modelDir, DAB_MODEL_FILE_NAME);
	}

	/*
	private File getSuffixModelFile(File modelDir) {
		return new File(modelDir, SUFFIX_MODEL_FILE_NAME);
	}
	*/

	static class AnalysisTask extends AnalysisTaskBase {
		private ExternalResourceDescription morphDictDesc;

		AnalysisTask(PartitionType targetPart,
				TypeSystemDescription inputTS,
				ExternalResourceDescription morphDictDesc) {
			super(PartitionType.DEV.equals(targetPart) ? "Analysis" : "AnalysisFinal",
					inputTS, targetPart);
			this.morphDictDesc = morphDictDesc;
		}

		@Override
		public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
				throws ResourceInitializationException, IOException {
			File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
			File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
			AnalysisEngineDescription goldRemoverDesc = createGoldRemoverDesc();
			AnalysisEngineDescription dabTaggerDesc = createPrimitiveDescription(
					DictionaryAwareBaselineTagger.class,
					DictionaryAwareBaselineTagger.PARAM_USE_DEBUG_GRAMMEMS, false,
					DictionaryAwareBaselineTagger.PARAM_NUM_GRAMMEME, MorphConstants.NUMR);
			/*
			AnalysisEngineDescription suffixTaggerDesc = createPrimitiveDescription(
					SuffixExaminingPosTagger.class,
					SuffixExaminingPosTagger.PARAM_USE_DEBUG_GRAMMEMS, false);
					*/
			// bind dictionary and wfStore resources
			ExternalResourceDescription dabWfStoreDesc = createExternalResourceDescription(
					SharedDefaultWordformStore.class,
					getDABModelFile(modelDir));
			/*
			ExternalResourceDescription suffixWfStoreDesc = createExternalResourceDescription(
					SharedDefaultWordformStore.class,
					getSuffixModelFile(modelDir));
					*/
			AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
			try {
				bindResource(dabTaggerDesc,
						DictionaryAwareBaselineTagger.RESOURCE_WFSTORE, dabWfStoreDesc);
				bindResource(dabTaggerDesc,
						DictionaryAwareBaselineTagger.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
				/*
				bindResource(suffixTaggerDesc,
						SuffixExaminingPosTagger.RESOURCE_WFSTORE, suffixWfStoreDesc);
				bindResource(suffixTaggerDesc,
						SuffixExaminingPosTagger.RESOURCE_MORPH_DICTIONARY, morphDictDesc);
						*/
			} catch (InvalidXMLException e) {
				throw new ResourceInitializationException(e);
			}
			return createAggregateDescription(goldRemoverDesc, dabTaggerDesc, /*suffixTaggerDesc,*/
					xmiWriterDesc);
		}
	}
}
