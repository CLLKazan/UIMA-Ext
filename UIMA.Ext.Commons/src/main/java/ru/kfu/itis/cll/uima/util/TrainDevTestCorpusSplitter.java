/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import static com.google.common.collect.Lists.newArrayList;
import static ru.kfu.itis.cll.uima.util.CorpusUtils.getDevPartitionFilename;
import static ru.kfu.itis.cll.uima.util.CorpusUtils.getTestPartitionFilename;
import static ru.kfu.itis.cll.uima.util.CorpusUtils.getTrainPartitionFilename;
import static ru.kfu.itis.cll.uima.util.CorpusUtils.partitionCorpusByFileSize;
import static ru.kfu.itis.cll.uima.util.CorpusUtils.toRelativePaths;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TrainDevTestCorpusSplitter {

	public static void main(String[] args) throws Exception {
		TrainDevTestCorpusSplitter launcher = new TrainDevTestCorpusSplitter();
		new JCommander(launcher, args);
		launcher.run();
	}

	@Parameter(names = "-p", required = true)
	private int partitionsNum;
	@Parameter(names = { "-s", "--corpus-file-suffix" })
	private String corpusFileSuffix;
	@Parameter(names = { "-c", "--corpus-dir" }, required = true)
	private File corpusDir;
	// output to current dir
	private File outputDir = new File(".");

	private TrainDevTestCorpusSplitter() {
	}

	private void run() throws Exception {
		FilenameFilter corpusFileFilter;
		if (corpusFileSuffix == null) {
			corpusFileFilter = FileFilterUtils.trueFileFilter();
		} else {
			corpusFileFilter = FileFilterUtils.suffixFileFilter(corpusFileSuffix);
		}
		List<Set<File>> partitions = newArrayList(partitionCorpusByFileSize(
				corpusDir, corpusFileFilter, partitionsNum));
		if (partitions.size() != partitionsNum) {
			throw new IllegalStateException();
		}
		// make dev partition from the last because it is a little bit smaller
		Set<File> devFiles = getAndRemove(partitions, partitions.size() - 1);
		Set<File> testFiles = getAndRemove(partitions, partitions.size() - 1);
		Set<File> trainFiles = Sets.newLinkedHashSet();
		for (Set<File> s : partitions) {
			trainFiles.addAll(s);
		}
		// write files
		File devPartFile = new File(outputDir, getDevPartitionFilename(0));
		FileUtils.writeLines(devPartFile, "utf-8", toRelativePaths(corpusDir, devFiles));
		File testPartFile = new File(outputDir, getTestPartitionFilename(0));
		FileUtils.writeLines(testPartFile, "utf-8", toRelativePaths(corpusDir, testFiles));
		File trainPartFile = new File(outputDir, getTrainPartitionFilename(0));
		FileUtils.writeLines(trainPartFile, "utf-8", toRelativePaths(corpusDir, trainFiles));
	}

	private static <T> T getAndRemove(List<T> list, int index) {
		T result = list.get(index);
		list.remove(index);
		return result;
	}
}
