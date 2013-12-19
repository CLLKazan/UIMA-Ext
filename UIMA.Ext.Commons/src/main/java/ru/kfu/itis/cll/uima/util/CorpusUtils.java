/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import static com.google.common.collect.Collections2.transform;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.comparator.SizeFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CorpusUtils {

	private static final String TRAINING_PARTITION_NAME_FORMAT = "training-%s.part";
	private static final String DEVELOPMENT_PARTITION_NAME_FORMAT = "dev-%s.part";
	private static final String TEST_PARTITION_NAME_FORMAT = "testing-%s.part";

	private static final Logger log = LoggerFactory.getLogger(CorpusUtils.class);

	public static enum PartitionType {
		TEST,
		DEV,
		TRAIN
	}

	public static String getDevPartitionFilename(int fold) {
		return String.format(DEVELOPMENT_PARTITION_NAME_FORMAT, fold);
	}

	public static String getTrainPartitionFilename(int fold) {
		return String.format(TRAINING_PARTITION_NAME_FORMAT, fold);
	}

	public static String getTestPartitionFilename(int fold) {
		return String.format(TEST_PARTITION_NAME_FORMAT, fold);
	}

	public static List<Set<File>> partitionCorpusByFileSize(File corpusDir,
			FilenameFilter corpusFileFilter, int partitionsNumber) {
		log.info("Partitioning corpus {} with filter {}...", corpusDir.getAbsolutePath(),
				corpusFileFilter);
		// TODO implement algorithm that is more robust to different file sizes
		// e.g. it should handle the case when there is no more files to include into the last partition
		if (partitionsNumber <= 0) {
			throw new IllegalArgumentException(String.format("Illegal number of partitions: %s",
					partitionsNumber));
		}
		if (!corpusDir.isDirectory()) {
			throw new IllegalArgumentException(String.format("%s is not existing directory",
					corpusDir));
		}
		final List<File> corpusFiles = Arrays.asList(corpusDir.listFiles(corpusFileFilter));
		// sort by decreasing size to smooth differences between parts
		Collections.sort(corpusFiles, SizeFileComparator.SIZE_REVERSE);
		int totalSize = 0;
		for (File cf : corpusFiles) {
			totalSize += cf.length();
		}
		log.info("Corpus total size (bytes): {}", totalSize);
		List<Set<File>> result = Lists.newArrayListWithExpectedSize(partitionsNumber);
		long[] partitionSizes = new long[partitionsNumber];
		// create empty parts
		for (int i = 0; i < partitionsNumber; i++) {
			result.add(Sets.<File> newLinkedHashSet());
		}
		int currentPartIdx = 0;
		for (File cf : corpusFiles) {
			result.get(currentPartIdx).add(cf);
			partitionSizes[currentPartIdx] = partitionSizes[currentPartIdx] + cf.length();
			currentPartIdx = (currentPartIdx + 1) % partitionsNumber;
		}
		// log
		log.info("Corpus {} has been partitioned by file sizes. Result partition sizes: {}",
				corpusDir, Arrays.toString(partitionSizes));
		// sanity checks
		if (result.size() != partitionsNumber || result.get(result.size() - 1).isEmpty()) {
			throw new IllegalStateException(
					"Illegal corpus partitioning result. Check previous log messages for details.");
		}
		return result;
	}

	public static List<CorpusSplit> createCrossValidationSplits(File corpusDir,
			FilenameFilter corpusFileFilter,
			final int foldsNum) {
		List<Set<File>> corpusPartitions = partitionCorpusByFileSize(corpusDir, corpusFileFilter,
				foldsNum);
		List<CorpusSplit> result = Lists.newArrayListWithExpectedSize(foldsNum);
		for (int f = 0; f < foldsNum; f++) {
			Set<File> trainingSet = Sets.newLinkedHashSet();
			Set<File> testingSet = corpusPartitions.get(f);
			// fill training set
			for (int i = 0; i < corpusPartitions.size(); i++) {
				if (i != f) {
					trainingSet.addAll(corpusPartitions.get(i));
				}
			}
			Function<File, String> relPathFunc = relativePathFunction(corpusDir);
			result.add(new DefaultCorpusSplit(
					transform(trainingSet, relPathFunc),
					transform(testingSet, relPathFunc)));
		}
		// sanity check
		if (result.size() != foldsNum) {
			throw new IllegalStateException();
		}
		return result;
	}

	static Function<File, String> relativePathFunction(final File baseDir) {
		return new Function<File, String>() {
			@Override
			public String apply(File arg) {
				String argPath = arg.getPath();
				String basePath = baseDir.getPath();
				// FIXME test on different OS
				if (!basePath.endsWith(File.separator)) {
					// to avoid result being absolute path
					basePath += File.separator;
				}
				if (argPath.startsWith(basePath)) {
					return argPath.substring(basePath.length());
				} else {
					throw new IllegalArgumentException(String.format(
							"File %s is not in dir %s", arg, baseDir));
				}
			}
		};
	}

	static Collection<String> toRelativePaths(File baseDir, Collection<File> files) {
		return transform(files, relativePathFunction(baseDir));
	}

	private CorpusUtils() {
	}
}