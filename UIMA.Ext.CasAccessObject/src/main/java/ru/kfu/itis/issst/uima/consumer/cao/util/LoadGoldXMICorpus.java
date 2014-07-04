/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao.util;

import static java.lang.System.err;
import static java.lang.System.exit;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_ANNO_TYPE;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_FEATURE_PATH;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_PATTERN;
import static ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer.PARAM_REPLACE_BY;
import static ru.kfu.itis.issst.uima.consumer.cao.CAOWriter.PARAM_DOC_METADATA_TYPE;
import static ru.kfu.itis.issst.uima.consumer.cao.CAOWriter.PARAM_DOC_METADATA_URI_FEATURE;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.uimafit.pipeline.SimplePipeline;

import ru.kfu.itis.cll.uima.annotator.FeatureValueReplacer;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LoadGoldXMICorpus {

	/**
	 * @param args
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static void main(String[] args) throws UIMAException, IOException {
		if (args.length != 3) {
			// TODO make uri replacement optional
			err.println("Usage: <typesystem-descriptor-names> <corpus-directory> " +
					"<caoWriter-desc-path>");
			exit(1);
		}
		;
		String tsdNamesString = args[0];
		String dirPath = args[1];
		if (!(new File(dirPath).isDirectory())) {
			err.println(String.format("%s - not a directory", dirPath));
			exit(1);
		}
		String caoWriterDescPath = args[2];

		String[] tsdNames = tsdNamesString.split(";");
		TypeSystemDescription tsd = createTypeSystemDescription(tsdNames);

		CollectionReaderDescription colReaderDesc = createDescription(XmiCollectionReader.class,
				tsd,
				XmiCollectionReader.PARAM_INPUTDIR, dirPath);

		XMLInputSource caoWriterDescInput = new XMLInputSource(caoWriterDescPath);
		XMLParser xmlParser = UIMAFramework.getXMLParser();
		AnalysisEngineDescription caoWriterDesc = xmlParser
				.parseAnalysisEngineDescription(caoWriterDescInput);

		ConfigurationParameterSettings caoWriterParams = caoWriterDesc.getAnalysisEngineMetaData()
				.getConfigurationParameterSettings();
		String metaAnnoType = (String) caoWriterParams.getParameterValue(PARAM_DOC_METADATA_TYPE);
		String metaAnnoUriFeaturePath = (String) caoWriterParams
				.getParameterValue(PARAM_DOC_METADATA_URI_FEATURE);
		AnalysisEngineDescription uriReplacerDesc = createPrimitiveDescription(
				FeatureValueReplacer.class, tsd,
				PARAM_ANNO_TYPE, metaAnnoType,
				PARAM_FEATURE_PATH, metaAnnoUriFeaturePath,
				PARAM_PATTERN, "file:.+/([^/]+)$",
				PARAM_REPLACE_BY, "$1");

		AnalysisEngineDescription tokenizerDesc = TokenizerAPI.getAEDescription();

		AnalysisEngineDescription sentSplitterDesc = SentenceSplitterAPI.getAEDescription();

		SimplePipeline.runPipeline(colReaderDesc,
				tokenizerDesc, sentSplitterDesc,
				uriReplacerDesc, caoWriterDesc);
	}
}