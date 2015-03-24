/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao.util;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_ANNO_TYPE;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_FEATURE_PATH;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_PATTERN;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_REPLACE_BY;
import static ru.kfu.itis.issst.uima.consumer.cao.CAOWriter.PARAM_DOC_METADATA_TYPE;
import static ru.kfu.itis.issst.uima.consumer.cao.CAOWriter.PARAM_DOC_METADATA_URI_FEATURE;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.fit.pipeline.SimplePipeline;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.consumer.cao.CAOWriter;
import ru.kfu.itis.issst.uima.consumer.cao.impl.MysqlJdbcCasAccessObject;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LoadGoldXMICorpus2 {

	public static void main(String[] args) throws UIMAException, IOException {
		Slf4jLoggerImpl.forceUsingThisImplementation();
		LoadGoldXMICorpus2 obj = new LoadGoldXMICorpus2();
		new JCommander(obj, args);
		obj.run();
	}

	@Parameter(names = { "--type-system-desc", "--tsd" }, required = true)
	private List<String> typeSystemDescriptorNames;

	@Parameter(names = { "-c" }, required = true)
	private File corpusDir;

	@Parameter(names = { "--ds" }, required = true)
	private File dsConfigFile;

	@Parameter(names = "--types-to-persist-file", required = true)
	private File ttpFile;

	private LoadGoldXMICorpus2() {
	}

	private void run() throws UIMAException, IOException {
		List<String> typesToPersist = FileUtils.readLines(ttpFile, "utf-8");
		// TODO trim and remove empty lines

		TypeSystemDescription tsd = createTypeSystemDescription(typeSystemDescriptorNames.toArray(
				new String[typeSystemDescriptorNames.size()]));

		CollectionReaderDescription colReaderDesc = createReaderDescription(XmiCollectionReader.class,
                tsd,
                XmiCollectionReader.PARAM_INPUTDIR, corpusDir.getPath());

		ExternalResourceDescription caoDesc = createExternalResourceDescription(
				MysqlJdbcCasAccessObject.class,
				dsConfigFile);

		AnalysisEngineDescription caoWriterDesc = createEngineDescription(CAOWriter.class,
                CAOWriter.RESOURCE_DAO, caoDesc,
                CAOWriter.PARAM_TYPES_TO_PERSIST, typesToPersist,
                CAOWriter.PARAM_SPAN_TYPE, Sentence.class.getName(),
                CAOWriter.PARAM_DOC_METADATA_DOCUMENT_SIZE, "documentSize");

		ConfigurationParameterSettings caoWriterParams = caoWriterDesc.getAnalysisEngineMetaData()
				.getConfigurationParameterSettings();
		String metaAnnoType = (String) caoWriterParams.getParameterValue(PARAM_DOC_METADATA_TYPE);
		String metaAnnoUriFeaturePath = (String) caoWriterParams
				.getParameterValue(PARAM_DOC_METADATA_URI_FEATURE);
		AnalysisEngineDescription uriReplacerDesc = createEngineDescription(
                FeatureValueReplacer.class, tsd,
                PARAM_ANNO_TYPE, metaAnnoType,
                PARAM_FEATURE_PATH, metaAnnoUriFeaturePath,
                PARAM_PATTERN, "file:.+/([^/]+)$",
                PARAM_REPLACE_BY, "$1");

		SimplePipeline.runPipeline(colReaderDesc,
				TokenizerAPI.getAEDescription(),
				SentenceSplitterAPI.getAEDescription(),
				uriReplacerDesc, caoWriterDesc);
	}
}