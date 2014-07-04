/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CrossValidationCorpusSplitter {

	public static void main(String[] args) throws Exception {
		CrossValidationCorpusSplitter launcher = new CrossValidationCorpusSplitter();
		new JCommander(launcher, args);
		launcher.run();
	}

	@Parameter(names = "-f", required = true)
	private int foldNum;
	@Parameter(names = { "-s", "--corpus-file-suffix" })
	private String corpusFileSuffix;
	@Parameter(names = { "-r", "-R" })
	private boolean includeSubDirectores = true;
	@Parameter(names = { "-c", "--corpus-dir" }, required = true)
	private File corpusDir;
	// output to current dir
	private File outputDir = new File(".");

	private CrossValidationCorpusSplitter() {
	}

	private void run() throws Exception {
		IOFileFilter corpusFileFilter;
		if (corpusFileSuffix == null) {
			corpusFileFilter = FileFilterUtils.trueFileFilter();
		} else {
			corpusFileFilter = FileFilterUtils.suffixFileFilter(corpusFileSuffix);
		}
		IOFileFilter corpusSubDirFilter = includeSubDirectores ? TrueFileFilter.INSTANCE : null;
		List<CorpusSplit> corpusSplits = CorpusUtils.createCrossValidationSplits(corpusDir,
				corpusFileFilter, corpusSubDirFilter, foldNum);
		for (int i = 0; i < corpusSplits.size(); i++) {
			writeFileLists(outputDir, i, corpusSplits.get(i));
		}
	}

	private void writeFileLists(File outputDir, int i, CorpusSplit corpusSplit)
			throws IOException {
		File trainingList = getTrainingListFile(outputDir, i);
		File testingList = getTestingListFile(outputDir, i);
		FileUtils.writeLines(trainingList, "utf-8", corpusSplit.getTrainingSetPaths());
		FileUtils.writeLines(testingList, "utf-8", corpusSplit.getTestingSetPaths());
	}

	public static File getTrainingListFile(File dir, int fold) {
		return new File(dir, CorpusUtils.getTrainPartitionFilename(fold));
	}

	public static File getTestingListFile(File dir, int fold) {
		return new File(dir, CorpusUtils.getTestPartitionFilename(fold));
	}
}
