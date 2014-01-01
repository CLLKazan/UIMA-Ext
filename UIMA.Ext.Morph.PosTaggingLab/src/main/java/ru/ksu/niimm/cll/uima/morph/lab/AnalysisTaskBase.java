/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.annotator.AnnotationRemover;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.cpe.XmiFileListReader;
import ru.kfu.itis.cll.uima.util.CorpusUtils;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.impl.UimaTaskBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class AnalysisTaskBase extends UimaTaskBase {

	// config fields
	private TypeSystemDescription inputTS;
	private PartitionType targetPartition;
	// state fields
	@Discriminator
	protected int fold;
	@Discriminator
	protected File corpusSplitInfoDir;

	public AnalysisTaskBase(String taskType, TypeSystemDescription inputTS,
			PartitionType targetPartition) {
		setType(taskType);
		this.inputTS = inputTS;
		this.targetPartition = targetPartition;
	}

	@Override
	public CollectionReaderDescription getCollectionReaderDescription(TaskContext taskCtx)
			throws ResourceInitializationException, IOException {
		File corpusDir = taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READONLY);
		File targetFileList = getTargetFileList();
		return CollectionReaderFactory.createDescription(XmiFileListReader.class, inputTS,
				XmiFileListReader.PARAM_BASE_DIR, corpusDir.getPath(),
				XmiFileListReader.PARAM_LIST_FILE, targetFileList.getPath());
	}

	protected AnalysisEngineDescription createGoldRemoverDesc()
			throws ResourceInitializationException {
		return createPrimitiveDescription(
				AnnotationRemover.class, inputTS,
				AnnotationRemover.PARAM_NAMESPACES_TO_REMOVE,
				Arrays.asList("org.opencorpora.cas"));
	}

	protected AnalysisEngineDescription createXmiWriterDesc(File outputDir)
			throws ResourceInitializationException {
		return createPrimitiveDescription(
				XmiWriter.class,
				XmiWriter.PARAM_OUTPUTDIR, outputDir);
	}

	private File getTargetFileList() {
		return getTargetFileList(corpusSplitInfoDir, targetPartition, fold);
	}

	static File getTargetFileList(File corpusSplitInfoDir, PartitionType targetPartition, int fold) {
		switch (targetPartition) {
		case DEV:
			return new File(corpusSplitInfoDir, CorpusUtils.getDevPartitionFilename(fold));
		case TEST:
			return new File(corpusSplitInfoDir, CorpusUtils.getTestPartitionFilename(fold));
		default:
			throw new UnsupportedOperationException("For PartitionType: " + targetPartition);
		}
	}
}
