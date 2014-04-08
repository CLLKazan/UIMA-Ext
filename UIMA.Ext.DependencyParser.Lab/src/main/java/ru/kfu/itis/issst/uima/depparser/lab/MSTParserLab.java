package ru.kfu.itis.issst.uima.depparser.lab;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.kfu.itis.cll.uima.io.IoUtils.openBufferedWriter;
import static ru.kfu.itis.cll.uima.util.CorpusUtils.getPartitionFilename;
import static ru.kfu.itis.issst.uima.depparser.mst.MSTFormat.writeInstance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import mstparser.DependencyInstance;
import mstparser.DependencyParser;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.uima.depparser.mst.MSTDependencyInstanceIterator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;
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

public class MSTParserLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/mstparser";
	private static final String KEY_CORPUS_DIR = "Corpus";
	private static final String KEY_MODEL_DIR = "Model";
	private static final String KEY_OUTPUT_DIR = "Output";

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", DEFAULT_WRK_DIR);
		MSTParserLab lab = new MSTParserLab();
		new JCommander(lab, args);
		lab.run();
	}

	@Parameter(names = "-f")
	private int foldsNum = 10;

	private MSTParserLab() {
	}

	private void run() throws Exception {
		// split corpus file
		Task corpusPartitioningTask = new ExecutableTaskBase() {
			{
				setType("CorpusPartitioning");
			}
			@Discriminator
			File corpusFile;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS_DIR, AccessMode.READWRITE);
				// get lines num
				int corpusSentences = calcSentenceNum(corpusFile);
				log.info("There are {} sentences in the corpus", corpusSentences);
				List<Range<Integer>> testRanges = makeCorpusSplits(corpusSentences, foldsNum);
				for (int fold = 0; fold < testRanges.size(); fold++) {
					Range<Integer> testRange = testRanges.get(fold);
					log.info("Writing {}-th fold where the test range is {}", fold, testRange);
					File trainFile = new File(corpusDir,
							getPartitionFilename(PartitionType.TRAIN, fold));
					File testFile = new File(corpusDir,
							getPartitionFilename(PartitionType.TEST, fold));
					BufferedWriter trainWriter = openBufferedWriter(trainFile);
					BufferedWriter testWriter = openBufferedWriter(testFile);
					MSTDependencyInstanceIterator instIter = new MSTDependencyInstanceIterator(
							corpusFile);
					try {
						int sent = -1;
						while (instIter.hasNext()) {
							DependencyInstance inst = instIter.next();
							sent++;
							if (testRange.contains(sent)) {
								writeInstance(testWriter, inst);
							} else {
								writeInstance(trainWriter, inst);
							}
						}
					} finally {
						closeQuietly(trainWriter);
						closeQuietly(testWriter);
						closeQuietly(instIter);
					}
				}
			}
		};
		// train
		Task trainingTask = new ExecutableTaskBase() {
			{
				setType("Training");
			}
			@Discriminator
			int fold;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS_DIR, AccessMode.READONLY);
				File trainFile = new File(corpusDir,
						getPartitionFilename(PartitionType.TRAIN, fold));
				//
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
				File modelFile = getModelFile(modelDir);
				//
				DependencyParser.main(new String[] { "train",
						"train-file:" + trainFile.getPath(),
						"format:MST",
						"model-name:" + modelFile.getPath()
				});
			}
		};
		// test
		Task testTask = new ExecutableTaskBase() {
			{
				setType("Testing");
			}
			@Discriminator
			int fold;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS_DIR, AccessMode.READONLY);
				File testFile = new File(corpusDir,
						getPartitionFilename(PartitionType.TEST, fold));
				//
				File modelDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
				File modelFile = getModelFile(modelDir);
				//
				File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
				File outputFile = getOutputFile(outputDir);
				//
				DependencyParser.main(new String[] { "test",
						"model-name:" + modelFile.getPath(),
						"test-file:" + testFile.getPath(),
						"format:MST",
						"output-file:" + outputFile.getPath()
				});
			}
		};
		// eval
		Task evalTask = new ExecutableTaskBase() {
			{
				setType("Evaluation");
			}
			@Discriminator
			int fold;

			@Override
			public void execute(TaskContext taskCtx) throws Exception {
				File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS_DIR, AccessMode.READONLY);
				File testFile = new File(corpusDir,
						getPartitionFilename(PartitionType.TEST, fold));
				//
				File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.ADD_ONLY);
				File outputFile = getOutputFile(outputDir);
				File evalReportFile = new File(outputDir, "eval-report.txt");
				//
				BufferedWriter evalReportWriter = openBufferedWriter(evalReportFile);
				try {
					evalReportWriter.write(String.format("Fold: %s%n", fold));
					DependencyEvaluator.evaluate(testFile.getPath(),
							outputFile.getPath(),
							"MST", false, evalReportWriter);
				} finally {
					closeQuietly(evalReportWriter);
				}
			}
		};
		// configure data-flow between tasks
		trainingTask.addImport(corpusPartitioningTask, KEY_CORPUS_DIR);
		testTask.addImport(corpusPartitioningTask, KEY_CORPUS_DIR);
		testTask.addImport(trainingTask, KEY_MODEL_DIR);
		evalTask.addImport(corpusPartitioningTask, KEY_CORPUS_DIR);
		evalTask.addImport(testTask, KEY_OUTPUT_DIR);
		//
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension("corpusFile"),
				Dimension.create("fold", makeFoldsDimension(foldsNum)));
		//
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(corpusPartitioningTask);
		batchTask.addTask(trainingTask);
		batchTask.addTask(testTask);
		batchTask.addTask(evalTask);
		batchTask.setParameterSpace(pSpace);
		batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
		Lab.getInstance().run(batchTask);
	}

	private static int calcSentenceNum(File file) throws IOException {
		MSTDependencyInstanceIterator instIterator = new MSTDependencyInstanceIterator(file);
		try {
			int result = 0;
			while (instIterator.hasNext()) {
				instIterator.next();
				result++;
			}
			return result;
		} finally {
			instIterator.close();
		}
	}

	private static Integer[] makeFoldsDimension(int foldNum) {
		Integer[] result = new Integer[foldNum];
		for (int i = 0; i < foldNum; i++) {
			result[i] = i;
		}
		return result;
	}

	/**
	 * @param sentenceNum
	 * @param foldsNum
	 * @return list where the i-th element represents a range of test sentences
	 *         in the corpus for the i-th split
	 */
	static List<Range<Integer>> makeCorpusSplits(int sentenceNum, int foldsNum) {
		if (foldsNum <= 1) {
			throw new IllegalArgumentException("foldsNum==" + foldsNum);
		}
		List<Range<Integer>> result = Lists.newArrayList();
		final int sentPerPart = sentenceNum / foldsNum;
		// make splits
		for (int split = 0; split < foldsNum; split++) {
			boolean lastSplit = split == foldsNum - 1;
			Range<Integer> testRange = Range.closedOpen(
					split * sentPerPart,
					lastSplit ? sentenceNum : (split + 1) * sentPerPart);
			result.add(testRange);
		}
		return result;
	}

	private static File getModelFile(File modelDir) {
		return new File(modelDir, "model.ser");
	}

	private static File getOutputFile(File outputDir) {
		return new File(outputDir, "output.txt");
	}
}
