/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_MODEL_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.cleartk.classifier.jar.DirectoryDataWriterFactory;
import org.cleartk.classifier.jar.JarClassifierBuilder;
import org.cleartk.classifier.jar.SequenceJarClassifierFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

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

import ru.kfu.itis.issst.cleartk.crfsuite.CRFSuiteStringOutcomeDataWriterFactory;
import ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPartitioningTask;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.FeatureExtractionTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TieredPosTaggerLab extends LabLauncherBase {

	public static void main(String[] args) throws IOException {
		System.setProperty("DKPRO_HOME", "wrk/tiered-pos-tagger");
		TieredPosTaggerLab lab = new TieredPosTaggerLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	@Parameter(names = { "-p", "--pos-tiers" }, required = true)
	private List<String> _posTiers;

	private TieredPosTaggerLab() {
	}

	// TODO (1) implement support of a PoS-tier consisting of several PoS-categories
	private void run() throws IOException {
		// prepare input TypeSystem
		final TypeSystemDescription inputTS = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		// prepare morph dictionary resource
		final ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				CachedSerializedDictionaryResource.class, "file:dict.opcorpora.ser");
		// create task instances
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		// -----------------------------------------------------------------
		Task corpusPartitioningTask = new CorpusPartitioningTask(foldsNum);
		// -----------------------------------------------------------------
		UimaTask featureExtractionTask = new FeatureExtractionTaskBase("FeatureExtraction", inputTS) {
			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File modelBaseDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
				List<AnalysisEngineDescription> posTaggerDescs = Lists.newArrayList();
				List<String> posTaggerNames = Lists.newArrayList();
				for (int i = 0; i < _posTiers.size(); i++) {
					String posTier = _posTiers.get(i);

					File modelDir = getModelDir(modelBaseDir, posTier);
					AnalysisEngineDescription ptDesc = createPrimitiveDescription(
							TieredPosSequenceAnnotator.class,
							TieredPosSequenceAnnotator.PARAM_POS_TIERS, _posTiers,
							TieredPosSequenceAnnotator.PARAM_CURRENT_TIER, i,
							TieredPosSequenceAnnotator.PARAM_DATA_WRITER_FACTORY_CLASS_NAME,
							CRFSuiteStringOutcomeDataWriterFactory.class.getName(),
							DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
							modelDir);
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
			int fold;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File modelBaseDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.ADD_ONLY);
				for (String posTier : _posTiers) {
					File modelDir = getModelDir(modelBaseDir, posTier);
					// FIXME
					JarClassifierBuilder.trainAndPackage(modelDir,
							"-a", "lbfgs",
							"-p", "max_iterations=70",
							"-p", "feature.minfreq=4");
				}
			}
		};
		// -----------------------------------------------------------------
		UimaTask analysisTask = new AnalysisTaskBase("Analysis", inputTS) {
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

					File modelDir = getModelDir(modelBaseDir, posTier);
					AnalysisEngineDescription ptDesc = createPrimitiveDescription(
							TieredPosSequenceAnnotator.class,
							TieredPosSequenceAnnotator.PARAM_POS_TIERS, _posTiers,
							TieredPosSequenceAnnotator.PARAM_CURRENT_TIER, i,
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
		Task evaluationTask = new EvaluationTask();
		// -----------------------------------------------------------------
		// configure data-flow between tasks
		corpusPartitioningTask.addImport(preprocessingTask, KEY_CORPUS);
		featureExtractionTask.addImport(corpusPartitioningTask, KEY_CORPUS);
		trainingTask.addImport(featureExtractionTask, KEY_MODEL_DIR);
		analysisTask.addImport(corpusPartitioningTask, KEY_CORPUS);
		analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
		evaluationTask.addImport(corpusPartitioningTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		// -----------------------------------------------------------------
		// create parameter space
		Integer[] foldValues = ContiguousSet.create(
				Range.closedOpen(0, foldsNum),
				DiscreteDomain.integers()).toArray(new Integer[0]);
		@SuppressWarnings("unchecked")
		ParameterSpace pSpace = new ParameterSpace(
				Dimension.create(DISCRIMINATOR_SOURCE_CORPUS_DIR, srcCorpusDir),
				// posCategories discriminator is used in the preprocessing task
				Dimension.create(DISCRIMINATOR_POS_CATEGORIES, Sets.newLinkedHashSet(_posTiers)),
				Dimension.create(DISCRIMINATOR_FOLD, foldValues));
		// -----------------------------------------------------------------
		// create and run BatchTask
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(corpusPartitioningTask);
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

	private File getModelDir(File modelBaseDir, String posTier) {
		// TODO escape chars that are not safe for filename
		return new File(modelBaseDir, posTier);
	}
}
