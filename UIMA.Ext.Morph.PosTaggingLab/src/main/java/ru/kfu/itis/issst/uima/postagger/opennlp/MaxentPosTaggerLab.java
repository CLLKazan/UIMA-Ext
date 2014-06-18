/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static ru.ksu.niimm.cll.uima.morph.lab.CorpusPartitioningTask.getTrainingListFile;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_MODEL_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.component.NoOpAnnotator;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;

import opennlp.tools.util.TrainingParameters;

import com.beust.jcommander.JCommander;

import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.lab.task.Task;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.impl.UimaTaskBase;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.cpe.AnnotationIteratorOverCollection;
import ru.kfu.itis.cll.uima.cpe.XmiFileListReader;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.LabConstants;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedDictionaryDeserializer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MaxentPosTaggerLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/opennlp-maxent-pos-lab";

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		MaxentPosTaggerLab lab = new MaxentPosTaggerLab();
		new JCommander(lab, args);
		lab.run();
	}

	private String languageCode = "RU";

	private MaxentPosTaggerLab() {
	}

	private void run() throws Exception {
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, gramModelDesc);
		//
		Task trainingTask = new ExecutableTaskBase() {
			{
				setType(generateTaskName("Training"));
			}

			@Discriminator
			Set<String> posCategories;
			@Discriminator
			int fold;
			@Discriminator
			File corpusSplitInfoDir;
			@Discriminator
			String trainingAlgorithm;
			@Discriminator
			int featureCutoff;
			@Discriminator
			int trainingIterations;
			@Discriminator
			int leftContextSize;
			@Discriminator
			int rightContextSize;
			@Discriminator
			int previousTagsInHistory;
			@Discriminator
			boolean generateDictionaryFeatures;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
				File trainingListFile = getTrainingListFile(corpusSplitInfoDir, fold);
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
				File modelFile = getModelFile(modelDir);
				//
				OpenNLPPosTaggerTrainer trainer = new OpenNLPPosTaggerTrainer();
				trainer.setLanguageCode(languageCode);
				trainer.setModelOutFile(modelFile);
				// set training parameters: algorithm, cutoff, iterations
				// TODO look at algorithm-specific params in TrainUtil class. E.g. 'threads' for MAXENT (with GIS)
				TrainingParameters trainParams = new TrainingParameters();
				trainParams.put(TrainingParameters.ALGORITHM_PARAM, trainingAlgorithm);
				trainParams.put(TrainingParameters.CUTOFF_PARAM, String.valueOf(featureCutoff));
				trainParams.put(TrainingParameters.ITERATIONS_PARAM,
						String.valueOf(trainingIterations));
				trainer.setTrainingParameters(trainParams);
				// configure sentence stream
				CollectionReaderDescription colReaderDesc = CollectionReaderFactory
						.createDescription(
								XmiFileListReader.class, inputTS,
								XmiFileListReader.PARAM_BASE_DIR, corpusDir.getPath(),
								XmiFileListReader.PARAM_LIST_FILE, trainingListFile.getPath());
				Iterator<Sentence> sentIter = AnnotationIteratorOverCollection.createIterator(
						Sentence.class, colReaderDesc,
						createPrimitiveDescription(NoOpAnnotator.class));
				SpanStreamOverCollection<Sentence> sentStream = new SpanStreamOverCollection<Sentence>(
						sentIter);
				trainer.setSentenceStream(sentStream);
				// configure tagger factory
				// // prepare dict if required
				MorphDictionary morphDict = null;
				if (generateDictionaryFeatures) {
					URL serDictUrl = UIMAFramework.newDefaultResourceManager()
							.resolveRelativePath(LabConstants.MORPH_DICT_FILENAME);
					// TODO handle cache key
					morphDict = CachedDictionaryDeserializer.getInstance()
							.getDictionary(serDictUrl, serDictUrl.openStream()).dictionary;
				}
				//
				trainer.setTaggerFactory(new POSTaggerFactory(
						new DefaultFeatureExtractors(
								previousTagsInHistory,
								leftContextSize, rightContextSize,
								posCategories,
								morphDict),
						// no need for a dictionary in training
						posCategories));
				// run
				trainer.train();
			}
		};
		//
		UimaTaskBase analysisTask = new AnalysisTask(inputTS, PartitionType.DEV, morphDictDesc);
		//
		Task evaluationTask = new EvaluationTask(PartitionType.DEV);
		// configure data-flow between tasks
		trainingTask.addImport(preprocessingTask, KEY_CORPUS);
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
				// tool-specific parameters
				getStringDimension("trainingAlgorithm"),
				getIntDimension("featureCutoff"),
				getIntDimension("trainingIterations"),
				getIntDimension("leftContextSize"),
				getIntDimension("rightContextSize"),
				getIntDimension("previousTagsInHistory"),
				getBoolDimension("generateDictionaryFeatures"),
				getIntDimension("beamSize"),
				getBoolDimension("beamSearchValidate")
				);
		//
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(trainingTask);
		batchTask.addTask(analysisTask);
		batchTask.addTask(evaluationTask);
		//
		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		Lab.getInstance().run(batchTask);
	}

	private static File getModelFile(File dir) {
		return new File(dir, "maxent-model.zip");
	}

	static class AnalysisTask extends AnalysisTaskBase {
		@Discriminator
		Integer beamSize;

		@Discriminator
		Boolean beamSearchValidate;

		private ExternalResourceDescription morphDictDesc;

		AnalysisTask(TypeSystemDescription inputTS,
				PartitionType targetPartition, ExternalResourceDescription morphDictDesc) {
			super(PartitionType.DEV.equals(targetPartition)
					? "MaxentPosTaggerLab.Analysis"
					: "MaxentPosTaggerLab.AnalysisFinal",
					inputTS, targetPartition);
			this.morphDictDesc = morphDictDesc;
		}

		@Override
		public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
				throws ResourceInitializationException, IOException {
			File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
			URL modelUrl = getModelFile(modelDir).getAbsoluteFile().toURI().toURL();
			File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
			// 
			AnalysisEngineDescription goldRemoverDesc = createGoldRemoverDesc();
			AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
			//
			Boolean beamSearchValidate = this.beamSearchValidate;
			if (beamSearchValidate == null) {
				beamSearchValidate = false;
			}
			AnalysisEngineDescription taggerDesc = OpenNLPPosTagger.createDescription(
					modelUrl.toString(),
					beamSearchValidate
							? DictionaryGrammemeLevelTokenSequenceValidator.class.getName()
							: null,
					beamSize);
			if (beamSearchValidate) {
				try {
					ExternalResourceFactory.createDependency(taggerDesc,
							DictionaryGrammemeLevelTokenSequenceValidator.RESOURCE_MORPH_DICT,
							MorphDictionaryHolder.class);
					ExternalResourceFactory.bindResource(taggerDesc,
							DictionaryGrammemeLevelTokenSequenceValidator.RESOURCE_MORPH_DICT,
							morphDictDesc);
				} catch (InvalidXMLException e) {
					throw new ResourceInitializationException(e);
				}
			}
			return createAggregateDescription(
					goldRemoverDesc, taggerDesc, xmiWriterDesc);
		}
	}
}
