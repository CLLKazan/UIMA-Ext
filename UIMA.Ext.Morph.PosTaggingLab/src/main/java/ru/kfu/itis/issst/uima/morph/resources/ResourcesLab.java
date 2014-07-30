/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.resources;

import static de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode.READONLY;
import static de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode.READWRITE;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static ru.kfu.itis.cll.uima.io.IoUtils.openPrintWriter;
import static ru.kfu.itis.cll.uima.io.IoUtils.openReader;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_FOLD;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_POS_CATEGORIES;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.DISCRIMINATOR_SOURCE_CORPUS_DIR;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.uima.morph.commons.TrainingDataWriterBase;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;

import com.beust.jcommander.JCommander;
import com.google.common.collect.Sets;

import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.reporting.ReportBase;
import de.tudarmstadt.ukp.dkpro.lab.task.Dimension;
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
public class ResourcesLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/resources";

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", new File(DEFAULT_WRK_DIR).getAbsolutePath());
		ResourcesLab lab = new ResourcesLab();
		new JCommander(lab, args);
		lab.run();
	}

	private ResourcesLab() {
	}

	private void run() throws Exception {
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, gramModelDesc);
		//
		UimaTask prepareTrainingSetInfo = new PrepareSplitInfoTask(
				"PrepareTrainingSetInfo", PartitionType.TRAIN, inputTS);
		prepareTrainingSetInfo.addReport(SegmentationReport.class);
		//
		UimaTask prepareDevSetInfo = new PrepareSplitInfoTask(
				"PrepareDevSetInfo", PartitionType.DEV, inputTS);
		prepareDevSetInfo.addReport(SegmentationReport.class);
		//
		UimaTask prepareTestSetInfo = new PrepareSplitInfoTask(
				"PrepareTestSetInfo", PartitionType.TEST, inputTS);
		prepareTestSetInfo.addReport(SegmentationReport.class);
		//
		Task collectUnseenWordsDev = new CollectUnseenWordsTask("CollectUnseenWordsDev");
		//
		Task collectUnseenWordsTest = new CollectUnseenWordsTask("CollectUnseenWordsTest");
		//
		prepareTrainingSetInfo.addImport(preprocessingTask, KEY_CORPUS);
		prepareDevSetInfo.addImport(preprocessingTask, KEY_CORPUS);
		prepareTestSetInfo.addImport(preprocessingTask, KEY_CORPUS);
		//
		collectUnseenWordsDev.addImport(prepareTrainingSetInfo, KEY_SPLIT_INFO_DIR,
				KEY_TRAINING_SPLIT_INFO_DIR);
		collectUnseenWordsDev.addImport(prepareDevSetInfo, KEY_SPLIT_INFO_DIR,
				KEY_TARGET_SPLIT_INFO_DIR);
		//
		collectUnseenWordsTest.addImport(prepareTrainingSetInfo, KEY_SPLIT_INFO_DIR,
				KEY_TRAINING_SPLIT_INFO_DIR);
		collectUnseenWordsTest.addImport(prepareTestSetInfo, KEY_SPLIT_INFO_DIR,
				KEY_TARGET_SPLIT_INFO_DIR);
		//
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension(DISCRIMINATOR_SOURCE_CORPUS_DIR),
				getFileDimension(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR),
				// posCategories discriminator is used in the preprocessing task
				getStringSetDimension(DISCRIMINATOR_POS_CATEGORIES),
				Dimension.create(DISCRIMINATOR_FOLD, 0));
		//
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(preprocessingTask);
		batchTask.addTask(prepareTrainingSetInfo);
		batchTask.addTask(prepareDevSetInfo);
		batchTask.addTask(prepareTestSetInfo);
		batchTask.addTask(collectUnseenWordsDev);
		batchTask.addTask(collectUnseenWordsTest);
		//
		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		Lab.getInstance().run(batchTask);
	}

	private static final String KEY_SPLIT_INFO_DIR = "SetInfo";
	private static final String KEY_TRAINING_SPLIT_INFO_DIR = "TrainingSetInfo";
	private static final String KEY_TARGET_SPLIT_INFO_DIR = "TargetSetInfo";

	private static File getInfoFile(File dir) {
		return new File(dir, TrainingDataWriterBase.TRAINING_DATA_FILENAME);
	}

	public static class SegmentationReport extends ReportBase {
		@Override
		public void execute() throws Exception {
			// String taskType = getContext().getMetadata().getType();
			File infoDir = getContext().getStorageLocation(KEY_SPLIT_INFO_DIR, READWRITE);
			File infoFile = getInfoFile(infoDir);
			BufferedReader infoReader = IoUtils.openReader(infoFile);
			//
			int sentenceNum = 0;
			int tokenNum = 0;
			int wordNum = 0;
			//
			try {
				String line;
				while ((line = infoReader.readLine()) != null) {
					if (line.isEmpty()) {
						sentenceNum++;
						continue;
					}
					tokenNum++;
					TokenInfo tokenInfo = TokenInfoWriter.parseLine(line);
					if (tokenInfo.isWord()) {
						wordNum++;
					}
				}
			} finally {
				IOUtils.closeQuietly(infoReader);
			}
			FileUtils.writeLines(new File(infoDir, "stats.txt"), Arrays.asList(
					format("Sentences\t%s", sentenceNum),
					format("Tokens\t%s", tokenNum),
					format("Words\t%s", wordNum)));
		}
	}

	private class PrepareSplitInfoTask extends CorpusSplitReadingTaskBase {

		public PrepareSplitInfoTask(
				String taskType, PartitionType targetSplit, TypeSystemDescription inputTS) {
			super(taskType, targetSplit, inputTS);
		}

		@Override
		public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
				throws ResourceInitializationException, IOException {
			File setInfoDir = taskCtx
					.getStorageLocation(KEY_SPLIT_INFO_DIR, READWRITE);
			AnalysisEngineDescription tokInfoWriterDesc = createPrimitiveDescription(
					TokenInfoWriter.class,
					TokenInfoWriter.PARAM_OUTPUT_DIR, setInfoDir);
			return createAggregateDescription(tokInfoWriterDesc);
		}
	}

	private class CollectUnseenWordsTask extends ExecutableTaskBase {
		CollectUnseenWordsTask(String taskType) {
			setType(taskType);
		}

		@Override
		public void execute(TaskContext taskCtx) throws Exception {
			File trainSetInfoDir = taskCtx.getStorageLocation(
					KEY_TRAINING_SPLIT_INFO_DIR, READONLY);
			File trainSetInfoFile = getInfoFile(trainSetInfoDir);
			log.info("Reading trainSetInfo...");
			Set<String> seenWords = collectWords(trainSetInfoFile);
			log.info("Finished reading trainSetInfo. Seen words: {}", seenWords.size());
			//
			File targetSetInfoDir = taskCtx
					.getStorageLocation(KEY_TARGET_SPLIT_INFO_DIR, READWRITE);
			File targetSetInfoFile = getInfoFile(targetSetInfoDir);
			File targetSetUnseenWordsFile = new File(targetSetInfoDir, "unseen-words.txt");
			log.info("Reading targetSetInfo...");
			BufferedReader targetSetReader = openReader(targetSetInfoFile);
			PrintWriter unseenWordsOut = openPrintWriter(targetSetUnseenWordsFile);
			int unseenWordNum = 0;
			try {
				String line;
				while ((line = targetSetReader.readLine()) != null) {
					if (line.isEmpty()) {
						continue;
					}
					TokenInfo tokInfo = TokenInfoWriter.parseLine(line);
					String tok = tokInfo.token;
					if (tokInfo.isWord() && !seenWords.contains(tok)) {
						unseenWordsOut.println(line);
						unseenWordNum++;
					}
				}
			} finally {
				closeQuietly(targetSetReader);
				closeQuietly(unseenWordsOut);
			}
			FileUtils.writeLines(new File(targetSetInfoDir, "stats.txt"), Arrays.asList(
					String.format("Unseen words\t%s", unseenWordNum)),
					true);
			log.info("Finished reading targetSetInfo. Unseen words: {}", unseenWordNum);
		}

		private Set<String> collectWords(File file) throws IOException {
			Set<String> seenWords = Sets.newHashSet();
			BufferedReader trainSetReader = openReader(file);
			try {
				String line;
				while ((line = trainSetReader.readLine()) != null) {
					if (line.isEmpty()) {
						continue;
					}
					TokenInfo tokInfo = TokenInfoWriter.parseLine(line);
					if (tokInfo.isWord()) {
						seenWords.add(tokInfo.token);
					}
				}
			} finally {
				closeQuietly(trainSetReader);
			}
			return seenWords;
		}
	}
}
