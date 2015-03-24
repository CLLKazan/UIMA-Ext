/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import static ru.ksu.niimm.cll.uima.morph.lab.CorpusPartitioningTask.getTrainingListFile;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;

import java.io.File;
import java.io.IOException;

import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.cpe.XmiFileListReader;

import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.impl.UimaTaskBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class FeatureExtractionTaskBase extends UimaTaskBase {

	// config fields
	private TypeSystemDescription inputTS;
	// state fields
	@Discriminator
	protected int fold;
	@Discriminator
	protected File corpusSplitInfoDir;

	protected FeatureExtractionTaskBase(String taskType, TypeSystemDescription inputTS) {
		setType(taskType);
		this.inputTS = inputTS;
	}

	@Override
	public CollectionReaderDescription getCollectionReaderDescription(TaskContext taskCtx)
			throws ResourceInitializationException, IOException {
		File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
		File trainingListFile = getTrainingListFile(corpusSplitInfoDir, fold);
		return CollectionReaderFactory.createReaderDescription(XmiFileListReader.class, inputTS,
				XmiFileListReader.PARAM_BASE_DIR, corpusDir.getPath(),
				XmiFileListReader.PARAM_LIST_FILE, trainingListFile.getPath());
	}
}
