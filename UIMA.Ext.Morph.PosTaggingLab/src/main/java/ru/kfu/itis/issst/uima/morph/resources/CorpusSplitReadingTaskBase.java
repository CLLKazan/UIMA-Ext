/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.resources;

import static ru.kfu.itis.cll.uima.util.CorpusUtils.getPartitionFilename;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;

import java.io.File;
import java.io.IOException;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.cpe.XmiFileListReader;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.impl.UimaTaskBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class CorpusSplitReadingTaskBase extends UimaTaskBase {

	// config fields
	private TypeSystemDescription inputTS;
	private PartitionType targetSplit;
	// state fields
	@Discriminator
	protected int fold;
	@Discriminator
	protected File corpusSplitInfoDir;

	protected CorpusSplitReadingTaskBase(String taskType, PartitionType targetSplit,
			TypeSystemDescription inputTS) {
		setType(taskType);
		this.targetSplit = targetSplit;
		this.inputTS = inputTS;
	}

	@Override
	public CollectionReaderDescription getCollectionReaderDescription(TaskContext taskCtx)
			throws ResourceInitializationException, IOException {
		File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
		File splitListFile = new File(corpusSplitInfoDir, getPartitionFilename(targetSplit, fold));
		return CollectionReaderFactory.createDescription(XmiFileListReader.class, inputTS,
				XmiFileListReader.PARAM_BASE_DIR, corpusDir.getPath(),
				XmiFileListReader.PARAM_LIST_FILE, splitListFile.getPath());
	}

}
