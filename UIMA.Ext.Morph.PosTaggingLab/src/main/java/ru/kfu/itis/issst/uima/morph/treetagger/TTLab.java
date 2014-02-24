/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static com.google.common.collect.Sets.newTreeSet;
import static de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode.READONLY;
import static de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode.READWRITE;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindResource;
import static ru.kfu.itis.issst.uima.morph.treetagger.DictionaryToTTLexicon.OPEN_CLASS_TAGS_FILENAME;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_MODEL_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_OUTPUT_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_TRAINING_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.MORPH_DICT_XML;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.annolab.tt4j.ExecutableResolver;
import org.annolab.tt4j.PlatformDetector;
import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.factory.ExternalResourceFactory;

import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.io.StreamGobblerBase;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.uima.morph.commons.DictionaryBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TagUtils;
import ru.kfu.itis.issst.uima.morph.treetagger.LexiconWriter.LexiconEntry;
import ru.ksu.niimm.cll.uima.morph.lab.AnalysisTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.EvaluationTask;
import ru.ksu.niimm.cll.uima.morph.lab.FeatureExtractionTaskBase;
import ru.ksu.niimm.cll.uima.morph.lab.LabConstants;
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
public class TTLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/tt-lab";

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		TTLab lab = new TTLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	private TTLab() {
	}

	private void run() throws Exception {
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		//
		Task prepareLexiconTask = new ExecutableTaskBase() {
			{
				setType("PrepareLexicon");
			}
			@Discriminator
			private Set<String> posCategories;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File ttLexiconDir = taskCtx.getStorageLocation(KEY_LEXICON_DIR, READWRITE);
				String ocHomeStr = System.getProperty("opencorpora.home");
				if (ocHomeStr == null) {
					throw new IllegalStateException("opencorpora.home is not set");
				}
				File ocHomeDir = new File(ocHomeStr);
				File dictXmlFile = new File(ocHomeDir, MORPH_DICT_XML);
				new DictionaryToTTLexicon(dictXmlFile, ttLexiconDir, posCategories).run();
			}
		};
		//
		UimaTask prepareTrainingDataTask = new FeatureExtractionTaskBase(
				"PrepareTrainingData", inputTS) {
			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File trainDataDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR, READWRITE);
				AnalysisEngineDescription ttTrainDataWriterDesc = createPrimitiveDescription(
						TTTrainingDataWriter.class,
						TTTrainingDataWriter.PARAM_OUTPUT_DIR, trainDataDir);
				try {
					ExternalResourceFactory.createDependency(ttTrainDataWriterDesc,
							DictionaryBasedTagMapper.RESOURCE_KEY_MORPH_DICTIONARY,
							MorphDictionaryHolder.class);
					bindResource(ttTrainDataWriterDesc,
							DictionaryBasedTagMapper.RESOURCE_KEY_MORPH_DICTIONARY, morphDictDesc);
				} catch (InvalidXMLException e) {
					throw new ResourceInitializationException(e);
				}
				return createAggregateDescription(ttTrainDataWriterDesc);
			}
		};
		//
		Task mergeLexiconTask = new ExecutableTaskBase() {
			{
				setType("MergeLexicon");
			}

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File trainingDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR, READWRITE);
				File tdLexFile = new File(trainingDir, TTTrainingDataWriter.LEXICON_FILENAME);
				File srcLexDir = taskCtx.getStorageLocation(KEY_LEXICON_DIR, READONLY);
				File srcLexFile = new File(srcLexDir, DictionaryToTTLexicon.LEXICON_FILENAME);
				File tmpLexFile = new File(trainingDir,
						TTTrainingDataWriter.LEXICON_FILENAME + ".tmp");
				LexiconWriter.mergeLexicons(srcLexFile, tdLexFile, tmpLexFile);
				// rename tmp file
				FileUtils.forceDelete(tdLexFile);
				if (!tmpLexFile.renameTo(tdLexFile)) {
					throw new IOException("Can't rename file" + tmpLexFile);
				}
				// HACK append an RNC-specific fake entry for null (NON-LEX) tag
				LexiconWriter.appendLexiconEntry(tdLexFile, "%FAKE_ENTRY%", NON_LEX_TAG);
				// enrich open-class-tagset
				File openClassTagsFile = new File(srcLexDir, OPEN_CLASS_TAGS_FILENAME);
				Set<String> openClassTags = newTreeSet(readLines(openClassTagsFile, "utf-8"));
				BufferedReader lexReader = IoUtils.openReader(tdLexFile);
				try {
					Iterator<LexiconEntry> lexIter = LexiconWriter.toIterator(lexReader);
					while (lexIter.hasNext()) {
						LexiconEntry le = lexIter.next();
						for (String tag : le.tags) {
							if (!TagUtils.isClosedClassTag(tag)) {
								if (openClassTags.add(tag)) {
									log.debug("Tag {} was added to open class tagset", tag);
								}
							}
						}
					}
				} finally {
					closeQuietly(lexReader);
				}
				openClassTags.add(NON_LEX_TAG);
				openClassTagsFile = new File(trainingDir, OPEN_CLASS_TAGS_FILENAME);
				FileUtils.writeLines(openClassTagsFile, "utf-8", openClassTags);
				// XXX adjust open class tags (null, Abbr ?)
			}
		};
		//
		Task trainingTask = new ExecutableTaskBase() {
			{
				setType("Training");
			}

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File trainDataDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR, READONLY);
				File trainDataFile = getTrainingDataFile(trainDataDir);
				File modelDir = taskCtx.getStorageLocation(LabConstants.KEY_MODEL_DIR, READWRITE);
				File modelFile = getModelFile(modelDir);
				File lexiconFile = new File(trainDataDir, TTTrainingDataWriter.LEXICON_FILENAME);
				File openClassTagsFile = new File(trainDataDir, OPEN_CLASS_TAGS_FILENAME);
				// get executable of trainer
				ExecutableResolver trainExeResolver = new TreeTaggerTrainExecutableResolver();
				trainExeResolver.setPlatformDetector(new PlatformDetector());
				// make cmd line
				List<String> cmd = Lists.newLinkedList();
				cmd.add(trainExeResolver.getExecutable());
				cmd.add(lexiconFile.getPath());
				cmd.add(openClassTagsFile.getPath());
				cmd.add(trainDataFile.getPath());
				cmd.add(modelFile.getPath());

				ProcessBuilder pb = new ProcessBuilder(cmd);
				pb.redirectErrorStream(true);
				Process trainProc = pb.start();
				// TODO destroy properly when trainProc is done (possible with error)
				StreamGobblerBase trainProcGobbler = StreamGobblerBase.toSystemOut(
						trainProc.getInputStream());
				new Thread(trainProcGobbler).start();
				int trainProcExitCode = trainProc.waitFor();
				// wait a little & stop gobbler
				Thread.sleep(1000);
				trainProcGobbler.done();
				if (trainProcExitCode != 0) {
					throw new IllegalStateException(String.format(
							"Tree-tagger trainer returned exit code: %s", trainProcExitCode));
				}
			}
		};
		//
		UimaTask analysisTask = new AnalysisTaskBase("Analysis", inputTS, PartitionType.DEV) {
			@Override
			public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
					throws ResourceInitializationException, IOException {
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
				File modelFile = getModelFile(modelDir);
				File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
				//
				AnalysisEngineDescription goldRemoverDesc = createGoldRemoverDesc();
				AnalysisEngineDescription ttDesc = createPrimitiveDescription(MorphTagger.class,
						MorphTagger.PARAM_TREETAGGER_MODEL_NAME, modelFile.getPath() + ":UTF-8",
						MorphTagger.PARAM_TAG_MAPPER_CLASS, DictionaryBasedTagMapper.class);
				try {
					bindResource(ttDesc, DictionaryBasedTagMapper.RESOURCE_KEY_MORPH_DICTIONARY,
							morphDictDesc);
				} catch (InvalidXMLException e) {
					throw new ResourceInitializationException(e);
				}
				AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
				return createAggregateDescription(goldRemoverDesc, ttDesc, xmiWriterDesc);
			}
		};
		//
		Task evaluationTask = new EvaluationTask(PartitionType.DEV);
		// configure data-flow between tasks
		prepareTrainingDataTask.addImport(preprocessingTask, KEY_CORPUS);
		mergeLexiconTask.addImport(prepareTrainingDataTask, KEY_TRAINING_DIR);
		mergeLexiconTask.addImport(prepareLexiconTask, KEY_LEXICON_DIR);
		trainingTask.addImport(mergeLexiconTask, KEY_TRAINING_DIR);
		analysisTask.addImport(preprocessingTask, KEY_CORPUS);
		analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
		evaluationTask.addImport(preprocessingTask, KEY_CORPUS);
		evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
		// create parameter space
		// TODO
		/*Integer[] foldValues = ContiguousSet.create(
				Range.closedOpen(0, foldsNum),
				DiscreteDomain.integers()).toArray(new Integer[0]);*/
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension(DISCRIMINATOR_SOURCE_CORPUS_DIR),
				// posCategories discriminator is used in the preprocessing task
				// FIXME add corpus split info dimension
				getStringSetDimension(DISCRIMINATOR_POS_CATEGORIES),
				Dimension.create(DISCRIMINATOR_FOLD, 0));
		//
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(prepareLexiconTask);
		batchTask.addTask(prepareTrainingDataTask);
		batchTask.addTask(mergeLexiconTask);
		batchTask.addTask(trainingTask);
		batchTask.addTask(analysisTask);
		batchTask.addTask(evaluationTask);
		//
		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		Lab.getInstance().run(batchTask);
	}

	private File getTrainingDataFile(File dir) {
		return new File(dir, TTTrainingDataWriter.TRAINING_DATA_FILENAME);
	}

	private File getModelFile(File dir) {
		return new File(dir, "tt.model");
	}

	private static final String KEY_LEXICON_DIR = "LexiconDir";

	private static final String NON_LEX_TAG = String.valueOf((Object) null);
}
