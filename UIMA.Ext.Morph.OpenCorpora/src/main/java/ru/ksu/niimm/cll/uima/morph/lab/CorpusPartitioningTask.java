/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

import ru.kfu.itis.cll.uima.util.CorpusSplit;
import ru.kfu.itis.cll.uima.util.CorpusUtils;

import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class CorpusPartitioningTask extends ExecutableTaskBase {

	{
		setType("CorpusPartioning");
	}
	// config fields
	private int foldsNum;
	// state fields
	@Discriminator
	File srcCorpusDir;

	public CorpusPartitioningTask(int foldsNum) {
		this.foldsNum = foldsNum;
	}

	@Override
	public void execute(TaskContext taskCtx) throws Exception {
		File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.ADD_ONLY);
		List<CorpusSplit> corpusSplits = CorpusUtils.createCrossValidationSplits(corpusDir,
				FileFilterUtils.suffixFileFilter(".xmi"), foldsNum);
		for (int i = 0; i < corpusSplits.size(); i++) {
			writeFileLists(corpusDir, i, corpusSplits.get(i));
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
