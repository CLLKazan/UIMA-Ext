/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import static de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode.READONLY;
import static de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode.READWRITE;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;
import static ru.kfu.itis.issst.uima.morph.hunpos.DefaultHunposExecutableResolver.trainerResolver;
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

import org.annolab.tt4j.ExecutableResolver;
import org.annolab.tt4j.PlatformDetector;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.factory.ExternalResourceFactory;

import ru.kfu.itis.cll.uima.io.ProcessIOUtils;
import ru.kfu.itis.cll.uima.io.StreamGobblerBase;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.uima.morph.commons.DictionaryBasedTagMapper;
import ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.FeatureExtractionTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

import com.beust.jcommander.JCommander;
import com.google.common.collect.Lists;

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
public class HunposLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/hunpos-lab";

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		HunposLab lab = new HunposLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	private HunposLab() {
	}

	private void run() throws Exception {
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		//
		UimaTask prepareTrainingDataTask = new FeatureExtractionTaskBase(
				"PrepareTrainingData", inputTS) {
			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File trainDataDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR, READWRITE);
				AnalysisEngineDescription hunposTrainDataWriterDesc = createPrimitiveDescription(
						HunposTrainingDataWriter.class,
						HunposTrainingDataWriter.PARAM_OUTPUT_DIR, trainDataDir);
				try {
					ExternalResourceFactory.createDependency(hunposTrainDataWriterDesc,
							DictionaryBasedTagMapper.RESOURCE_KEY_MORPH_DICTIONARY,
							MorphDictionaryHolder.class);
					bindResource(hunposTrainDataWriterDesc,
							DictionaryBasedTagMapper.RESOURCE_KEY_MORPH_DICTIONARY, morphDictDesc);
				} catch (InvalidXMLException e) {
					throw new ResourceInitializationException(e);
				}
				return createAggregateDescription(hunposTrainDataWriterDesc);
			}
		};
		//
		Task trainingTask = new ExecutableTaskBase() {
			{
				setType("Training");
			}

			@Discriminator
			int tagOrder;
			@Discriminator
			int emissionOrder;
			@Discriminator
			int rareWordFrequency;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File trainDataDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR, READONLY);
				File trainDataFile = getTrainingDataFile(trainDataDir);
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, READWRITE);
				File modelFile = getModelFile(modelDir);
				// get executable of trainer
				ExecutableResolver trainExeResolver = trainerResolver();
				trainExeResolver.setPlatformDetector(new PlatformDetector());
				// make cmd line
				List<String> cmd = Lists.newLinkedList();
				cmd.add(trainExeResolver.getExecutable());
				// tag order, -t
				cmd.add("-t" + tagOrder);
				// emission order, -e
				cmd.add("-e" + emissionOrder);
				// rare word frequency threshold, -f
				cmd.add("-f" + rareWordFrequency);
				// result model file
				cmd.add(modelFile.getPath());
				// start trainer process
				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.redirectErrorStream(true);
				Process trainProc = pb.start();
				// attach stdout & stderr streams gobbler
				StreamGobblerBase trainProcGobbler = StreamGobblerBase.toSystemOut(
						trainProc.getInputStream());
				new Thread(trainProcGobbler).start();
				// feed training data to stdin
				ProcessIOUtils.feedProcessInput(trainProc, trainDataFile, true);
				// wait for the end of training
				int trainProcExitCode;
				try {
					trainProcExitCode = trainProc.waitFor();
					// wait a little & stop gobbler
					Thread.sleep(1000);
				} finally {
					trainProcGobbler.done();
				}
				if (trainProcExitCode != 0) {
					throw new IllegalStateException(String.format(
							"Hunpos trainer returned exit code: %s", trainProcExitCode));
				}
			}
		};
		//
		UimaTask analysisTask = new AnalysisTask(PartitionType.DEV, inputTS, morphDictDesc);
		//
		Task evaluationTask = new EvaluationTask(PartitionType.DEV);
		// configure data-flow between tasks
		prepareTrainingDataTask.addImport(preprocessingTask, KEY_CORPUS);
		trainingTask.addImport(prepareTrainingDataTask, KEY_TRAINING_DIR);
		analysisTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
		evaluationTask.addImport(preprocessingTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		// create parameter space
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension(DISCRIMINATOR_SOURCE_CORPUS_DIR),
				getFileDimension(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR),
				// posCategories discriminator is used in the preprocessing task
				getStringSetDimension(DISCRIMINATOR_POS_CATEGORIES),
				Dimension.create(DISCRIMINATOR_FOLD, 0),
				// model-specific parameters
				getIntDimension("tagOrder"),
				getIntDimension("emissionOrder"),
				getIntDimension("rareWordFrequency"),
				getFileDimension("lexiconFile")
				);
		//
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(prepareTrainingDataTask);
		batchTask.addTask(trainingTask);
		batchTask.addTask(analysisTask);
		batchTask.addTask(evaluationTask);
		//
		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		Lab.getInstance().run(batchTask);
	}

	private File getTrainingDataFile(File dir) {
		return new File(dir, HunposTrainingDataWriter.TRAINING_DATA_FILENAME);
	}

	private static File getModelFile(File dir) {
		return new File(dir, "hunpos.model");
	}

	static class AnalysisTask extends AnalysisTaskBase {
		// config fields
		private ExternalResourceDescription morphDictDesc;
		// discriminators
		@Discriminator
		File lexiconFile;

		AnalysisTask(PartitionType targetPartition, TypeSystemDescription inputTS,
				ExternalResourceDescription morphDictDesc) {
			super(PartitionType.DEV.equals(targetPartition) ? "Analysis" : "AnalysisFinal",
					inputTS, targetPartition);
			this.morphDictDesc = morphDictDesc;
		}

		@Override
		public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
				throws ResourceInitializationException, IOException {
			File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
			File modelFile = getModelFile(modelDir);
			File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
			//
			AnalysisEngineDescription goldRemoverDesc = createGoldRemoverDesc();
			AnalysisEngineDescription hunposAnnotatorDesc = createPrimitiveDescription(
					HunposAnnotator.class,
					HunposAnnotator.PARAM_HUNPOS_MODEL_NAME, modelFile.getPath(),
					HunposAnnotator.PARAM_TAG_MAPPER_CLASS,
					DictionaryBasedTagMapper.class.getName(),
					HunposAnnotator.PARAM_LEXICON_FILE, lexiconFile);
			try {
				ExternalResourceFactory.createDependency(hunposAnnotatorDesc,
						DictionaryBasedTagMapper.RESOURCE_KEY_MORPH_DICTIONARY,
						MorphDictionaryHolder.class);
				bindResource(hunposAnnotatorDesc,
						DictionaryBasedTagMapper.RESOURCE_KEY_MORPH_DICTIONARY,
						morphDictDesc);
			} catch (InvalidXMLException e) {
				throw new ResourceInitializationException(e);
			}
			AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
			return createAggregateDescription(
					goldRemoverDesc, hunposAnnotatorDesc, xmiWriterDesc);
		}
	}
}
