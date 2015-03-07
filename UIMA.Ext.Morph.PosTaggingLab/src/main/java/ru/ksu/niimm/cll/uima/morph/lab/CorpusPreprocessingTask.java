/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.Discriminator;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.impl.UimaTaskBase;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.issst.uima.morph.commons.GramModelBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.kfu.itis.issst.uima.postagger.PosTrimmingAnnotator;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.bindExternalResource;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.KEY_CORPUS;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CorpusPreprocessingTask extends UimaTaskBase {

	{
		setType("CorpusPreProcessing");
	}
	// config fields
	private TypeSystemDescription inputTS;
	private ExternalResourceDescription gramModelDesc;

	public CorpusPreprocessingTask(TypeSystemDescription inputTS,
			ExternalResourceDescription gramModelDesc) {
		this.inputTS = inputTS;
		this.gramModelDesc = gramModelDesc;
	}

	// state fields
	@Discriminator
	Set<String> posCategories;
	@Discriminator
	File srcCorpusDir;

	@Override
	public CollectionReaderDescription getCollectionReaderDescription(TaskContext taskCtx)
			throws ResourceInitializationException, IOException {
		return CollectionReaderFactory.createReaderDescription(XmiCollectionReader.class,
                inputTS,
                XmiCollectionReader.PARAM_INPUTDIR, srcCorpusDir.getPath());
	}

	@Override
	public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
			throws ResourceInitializationException, IOException {
		AnalysisEngineDescription posTrimmerDesc = createEngineDescription(
                PosTrimmingAnnotator.class, inputTS,
                PosTrimmingAnnotator.PARAM_TARGET_POS_CATEGORIES, posCategories,
                PosTrimmingAnnotator.RESOURCE_GRAM_MODEL, gramModelDesc);
		//
		AnalysisEngineDescription tagAssemblerDesc = TagAssembler.createDescription();
		bindExternalResource(tagAssemblerDesc,
				GramModelBasedTagMapper.RESOURCE_GRAM_MODEL, gramModelDesc);
		//
		AnalysisEngineDescription xmiWriterDesc = XmiWriter.createDescription(
				taskCtx.getStorageLocation(KEY_CORPUS, AccessMode.READWRITE),
				// write to relative path
				true);
		//
		return createEngineDescription(posTrimmerDesc, tagAssemblerDesc, xmiWriterDesc);
	}

}
