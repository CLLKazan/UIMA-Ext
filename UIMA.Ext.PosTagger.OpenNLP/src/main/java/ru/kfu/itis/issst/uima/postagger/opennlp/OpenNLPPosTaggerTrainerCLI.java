/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory.getMorphDictionaryAPI;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import opennlp.tools.util.TrainingParameters;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.cpe.AnnotationIteratorOverCollection;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmingAnnotator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OpenNLPPosTaggerTrainerCLI {

	static {
		Slf4jLoggerImpl.forceUsingThisImplementation();
	}

	public static void main(String[] args) throws Exception {
		OpenNLPPosTaggerTrainerCLI cli = new OpenNLPPosTaggerTrainerCLI();
		new JCommander(cli, args);
		//
		OpenNLPPosTaggerTrainer trainer = new OpenNLPPosTaggerTrainer();
		trainer.setLanguageCode(cli.languageCode);
		trainer.setModelOutFile(cli.modelOutFile);
		// train params
		{
			FileInputStream fis = FileUtils.openInputStream(cli.trainParamsFile);
			TrainingParameters trainParams;
			try {
				trainParams = new TrainingParameters(fis);
			} finally {
				IOUtils.closeQuietly(fis);
			}
			trainer.setTrainingParameters(trainParams);
		}
		// feature extractors
		{
			FileInputStream fis = FileUtils.openInputStream(cli.extractorParams);
			Properties props = new Properties();
			try {
				props.load(fis);
			} finally {
				IOUtils.closeQuietly(fis);
			}
			// TODO dict param
			trainer.setTaggerFactory(new POSTaggerFactory(
					DefaultFeatureExtractors.from(props, null),
					null));
		}
		// input sentence stream
		{
			ExternalResourceDescription morphDictDesc = getMorphDictionaryAPI()
					.getResourceDescriptionForCachedInstance();
			TypeSystemDescription tsd = createTypeSystemDescription(
					"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
					TokenizerAPI.TYPESYSTEM_TOKENIZER,
					SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
					"org.opencorpora.morphology-ts");
			CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createDescription(
					XmiCollectionReader.class, tsd,
					XmiCollectionReader.PARAM_INPUTDIR, cli.trainingXmiDir);
			AnalysisEngineDescription posTrimmerDesc = PosTrimmingAnnotator.createDescription(
					cli.gramCategories.toArray(new String[0]), morphDictDesc);
			AnalysisEngineDescription tagAssemblerDesc = TagAssembler
					.createDescription(morphDictDesc);
			AnalysisEngineDescription aeDesc = createAggregateDescription(
					posTrimmerDesc, tagAssemblerDesc);
			Iterator<Sentence> sentIter = AnnotationIteratorOverCollection.createIterator(
					Sentence.class, colReaderDesc, aeDesc);
			SpanStreamOverCollection<Sentence> sentStream = new SpanStreamOverCollection<Sentence>(
					sentIter);
			trainer.setSentenceStream(sentStream);
		}
		trainer.train();
	}

	@Parameter(names = "-l")
	private String languageCode = "RU";
	@Parameter(names = { "-o", "--output-file" }, required = true)
	private File modelOutFile;
	@Parameter(names = "--train-params", required = true)
	private File trainParamsFile;
	@Parameter(names = "--extractor-params", required = true)
	private File extractorParams;
	// input PoS-stream config fields
	@Parameter(names = "--gram-categories", required = true)
	private List<String> gramCategories;
	@Parameter(names = { "-c", "--corpus-dir" }, required = true)
	private File trainingXmiDir;

	private OpenNLPPosTaggerTrainerCLI() {
	}
}
