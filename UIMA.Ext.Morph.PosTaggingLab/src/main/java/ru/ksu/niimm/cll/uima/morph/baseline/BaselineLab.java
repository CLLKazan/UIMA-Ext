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
public class BaselineLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/freq-baseline";
	private static final String BASELINE_MODEL_FILE_NAME = "baseline.ser";
	private static final String SUFFIX_MODEL_FILE_NAME = "suffix.ser";

	public static void main(String[] args) throws IOException {
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		BaselineLab lab = new BaselineLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	private BaselineLab() {
	}

	private void run() throws IOException {
		//
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, gramModelDesc);
		//
		UimaTask trainingTask = new FeatureExtractionTaskBase("Training", inputTS) {
			@Discriminator
			int suffixLength;

			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
				AnalysisEngineDescription baselineLearnerDesc = createPrimitiveDescription(
						BaselineLearner.class, inputTS,
						BaselineLearner.PARAM_MODEL_OUTPUT_FILE,
						getFreqModelFile(modelDir));
				AnalysisEngineDescription suffixModelTrainerDesc = createPrimitiveDescription(
						SuffixExaminingPosTrainer.class,
						SuffixExaminingPosTrainer.PARAM_WFSTORE_FILE, getSuffixModelFile(modelDir),
						SuffixExaminingPosTrainer.PARAM_SUFFIX_LENGTH, suffixLength);
				return createAggregateDescription(baselineLearnerDesc, suffixModelTrainerDesc);
			}
		};
		//
		UimaTask analysisTask = new AnalysisTask(PartitionType.DEV, inputTS);
		// 
		Task evaluationTask = new EvaluationTask(PartitionType.DEV);
		// configure data-flow between tasks
		trainingTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
		evaluationTask.addImport(preprocessingTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		// -----------------------------------------------------------------
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension(DISCRIMINATOR_SOURCE_CORPUS_DIR),
				getFileDimension(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR),
				getStringSetDimension(DISCRIMINATOR_POS_CATEGORIES),
				Dimension.create(DISCRIMINATOR_FOLD, 0),
				// model-specific parameters
				getIntDimension("suffixLength"));
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

	private static File getFreqModelFile(File modelDir) {
		return new File(modelDir, BASELINE_MODEL_FILE_NAME);
	}

	private static File getSuffixModelFile(File modelDir) {
		return new File(modelDir, SUFFIX_MODEL_FILE_NAME);
	}

	static class AnalysisTask extends AnalysisTaskBase {
		AnalysisTask(PartitionType targetPartition,
				TypeSystemDescription inputTS) {
			super(PartitionType.DEV.equals(targetPartition) ? "Analysis" : "AnalysisFinal",
					inputTS, targetPartition);
		}

		@Override
		public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
				throws ResourceInitializationException, IOException {
			File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
			File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
			AnalysisEngineDescription goldRemoverDesc = createGoldRemoverDesc();
			AnalysisEngineDescription baselineTaggerDesc = createPrimitiveDescription(
					BaselineTagger.class,
					BaselineTagger.PARAM_NUM_GRAMMEME, MorphConstants.NUMR);
			AnalysisEngineDescription suffixTaggerDesc = createPrimitiveDescription(
					SuffixExaminingPosTagger.class,
					SuffixExaminingPosTagger.PARAM_USE_DEBUG_GRAMMEMS, false);
			// bind dictionary and wfStore resources
			ExternalResourceDescription freqWfStoreDesc = createExternalResourceDescription(
					SharedDefaultWordformStore.class,
					getFreqModelFile(modelDir));
			ExternalResourceDescription suffixWfStoreDesc = createExternalResourceDescription(
					SharedDefaultWordformStore.class,
					getSuffixModelFile(modelDir));
			AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
			try {
				bindResource(baselineTaggerDesc,
						BaselineTagger.RESOURCE_WFSTORE, freqWfStoreDesc);
				bindResource(suffixTaggerDesc,
						SuffixExaminingPosTagger.RESOURCE_WFSTORE, suffixWfStoreDesc);
			} catch (InvalidXMLException e) {
				throw new ResourceInitializationException(e);
			}
			return createAggregateDescription(goldRemoverDesc, baselineTaggerDesc,
					suffixTaggerDesc,
					xmiWriterDesc);
		}
	}
}
