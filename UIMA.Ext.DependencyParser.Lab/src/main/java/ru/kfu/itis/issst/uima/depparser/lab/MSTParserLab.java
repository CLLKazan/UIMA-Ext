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
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.uima.depparser.mst.MSTDependencyInstanceIterator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.task.ParameterSpace;
import de.tudarmstadt.ukp.dkpro.lab.task.Task;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask.ExecutionPolicy;

public class MSTParserLab extends LabLauncherBase {

	static final String DEFAULT_WRK_DIR = "wrk/mstparser";
	public static final String KEY_CORPUS_DIR = "Corpus";

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
		//
		ParameterSpace pSpace = new ParameterSpace(
				getFileDimension("corpusFile"));
		//
		BatchTask batchTask = new BatchTask();
		batchTask.addTask(corpusPartitioningTask);
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
}
