/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import static com.google.common.collect.Collections2.transform;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.SizeFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
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

	public static String getPartitionFilename(PartitionType partType, int fold) {
		switch (partType) {
		case DEV:
			return getDevPartitionFilename(fold);
		case TEST:
			return getTestPartitionFilename(fold);
		case TRAIN:
			return getTrainPartitionFilename(fold);
		default:
			throw new UnsupportedOperationException();
		}
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

	/**
	 * Partition corpus files specified by filters.
	 * 
	 * @param corpusDir
	 *            corpus base directory
	 * @param corpusFileFilter
	 *            filter for corpus files
	 * @param corpusSubDirFilter
	 *            filter for corpus subdirectories. If null subdirectories will
	 *            be ignored.
	 * @param partitionsNumber
	 * @return list of file sets (partitions)
	 */
	public static List<Set<File>> partitionCorpusByFileSize(File corpusDir,
			IOFileFilter corpusFileFilter, IOFileFilter corpusSubDirFilter,
			int partitionsNumber) {
		log.info("Partitioning corpus {} with file filter {} and subdir filter {}...",
				new Object[] { corpusDir.getAbsolutePath(), corpusFileFilter, corpusSubDirFilter });
		// TODO implement an algorithm that is more robust to different file sizes
		// e.g. it should handle the case when there is no more files to include into the last partition
		if (partitionsNumber <= 0) {
			throw new IllegalArgumentException(String.format("Illegal number of partitions: %s",
					partitionsNumber));
		}
		if (!corpusDir.isDirectory()) {
			throw new IllegalArgumentException(String.format("%s is not existing directory",
					corpusDir));
		}
		final Deque<File> corpusFilesDeq;
		{
			List<File> corpusFiles = Lists.newArrayList(
					FileUtils.listFiles(corpusDir, corpusFileFilter, corpusSubDirFilter));
			// sort by decreasing size to smooth differences between parts
			Collections.sort(corpusFiles, SizeFileComparator.SIZE_REVERSE);
			corpusFilesDeq = Lists.newLinkedList(corpusFiles);
		}
		//
		int totalSize = 0;
		for (File cf : corpusFilesDeq) {
			totalSize += cf.length();
		}
		log.info("Corpus total size (bytes): {}", totalSize);
		List<FileBucket> buckets = Lists.newArrayListWithExpectedSize(partitionsNumber);
		// create empty parts
		for (int i = 0; i < partitionsNumber; i++) {
			buckets.add(new FileBucket());
		}
		while (!corpusFilesDeq.isEmpty()) {
			File cf = corpusFilesDeq.pop();
			buckets.get(0).add(cf);
			// resort: make the least bucket first
			Collections.sort(buckets);
		}
		// resort: make the largest bucket first
		Collections.sort(buckets, Collections.reverseOrder());
		// log
		log.info("Corpus {} has been partitioned by file sizes. Result partitions:\n{}",
				corpusDir, Joiner.on('\n').join(buckets));
		// transform
		List<Set<File>> result = Lists.newArrayList();
		for (FileBucket b : buckets) {
			result.add(b.getFiles());
		}
		// sanity checks
		if (result.size() != partitionsNumber || result.get(result.size() - 1).isEmpty()) {
			throw new IllegalStateException(
					"Illegal corpus partitioning result. Check previous log messages for details.");
		}
		return result;
	}

	/**
	 * convenience overload of
	 * {@link #partitionCorpusByFileSize(File, IOFileFilter, IOFileFilter, int)}
	 * that look up corpus files in all subdirectories.
	 * 
	 * @param corpusDir
	 * @param corpusFileFilter
	 * @param partitionsNumber
	 * @return
	 */
	public static List<Set<File>> partitionCorpusByFileSize(File corpusDir,
			IOFileFilter corpusFileFilter, int partitionsNumber) {
		return partitionCorpusByFileSize(corpusDir, corpusFileFilter, TrueFileFilter.INSTANCE,
				partitionsNumber);
	}

	public static List<CorpusSplit> createCrossValidationSplits(File corpusDir,
			IOFileFilter corpusFileFilter, IOFileFilter corpusSubDirFilter,
			final int foldsNum) {
		List<Set<File>> corpusPartitions = partitionCorpusByFileSize(
				corpusDir, corpusFileFilter, corpusSubDirFilter, foldsNum);
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

	/**
	 * @param baseDir
	 * @return function that returns the path of an arg File relative to the
	 *         given baseDir. It works only with the assumption that an arg File
	 *         is within baseDir. This is usually true to deal with a corpus
	 *         structure.
	 */
	public static Function<File, String> relativePathFunction(final File baseDir) {
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

	/**
	 * @param baseDir
	 * @return function that returns the URI of an arg File relative to the URI
	 *         of the given baseDir.
	 * @see #relativePathFunction(File)
	 */
	public static Function<File, URI> relativeURIFunction(final File baseDir) {
		return new Function<File, URI>() {
			private URI baseURI = baseDir.toURI();

			@Override
			public URI apply(File arg) {
				URI relURI = baseURI.relativize(arg.toURI());
				try {
					return new URI("file", relURI.getPath(), null);
				} catch (URISyntaxException e) {
					throw new IllegalStateException(e);
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