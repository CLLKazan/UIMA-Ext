/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.bindExternalResource;
import static org.uimafit.factory.ExternalResourceFactory.createDependency;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static ru.ksu.niimm.cll.uima.morph.ruscorpora.DictionaryAligningTagMapper2.RESOURCE_KEY_MORPH_DICTIONARY;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;

import ru.kfu.itis.cll.uima.annotator.AnnotationRemover;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.InitialTokenizer;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;
import ru.ksu.niimm.cll.uima.morph.util.NonTokenizedSpan;
import ru.ksu.niimm.cll.uima.morph.util.NonTokenizedSpanAnnotator;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class RusCorporaParserBootstrap {

	public static void main(String[] args) throws Exception {
		// setup logging
		Slf4jLoggerImpl.forceUsingThisImplementation();
		//
		RusCorporaParserBootstrap launcher = new RusCorporaParserBootstrap();
		new JCommander(launcher, args);
		launcher.run();
	}

	@Parameter(names = "--ruscorpora-text-dir", required = true)
	private File ruscorporaTextDir;
	@Parameter(names = { "-o", "--output-dir" }, required = true)
	private File xmiOutputDir;
	@Parameter(names = "--enable-dictionary-aligning")
	private boolean enableDictionaryAligning;

	private RusCorporaParserBootstrap() {
	}

	private void run() throws Exception {
		CollectionReaderDescription colReaderDesc;
		{
			TypeSystemDescription tsDesc = TypeSystemDescriptionFactory
					.createTypeSystemDescription(
							"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
							TokenizerAPI.TYPESYSTEM_TOKENIZER,
							SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
							"org.opencorpora.morphology-ts");
			//
			if (!enableDictionaryAligning) {
				colReaderDesc = CollectionReaderFactory.createDescription(
						RusCorporaCollectionReader.class,
						tsDesc,
						RusCorporaCollectionReader.PARAM_INPUT_DIR, ruscorporaTextDir.getPath());
			} else {
				File daLogFile = new File(xmiOutputDir, "dict-aligning2.log");
				colReaderDesc = CollectionReaderFactory.createDescription(
						RusCorporaCollectionReader.class,
						tsDesc,
						RusCorporaCollectionReader.PARAM_INPUT_DIR, ruscorporaTextDir.getPath(),
						RusCorporaCollectionReader.PARAM_TAG_MAPPER_CLASS,
						DictionaryAligningTagMapper2.class,
						DictionaryAligningTagMapper2.PARAM_OUT_FILE, daLogFile.getPath());
				ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
						CachedSerializedDictionaryResource.class,
						"file:dict.opcorpora.ser");
				createDependency(colReaderDesc,
						RESOURCE_KEY_MORPH_DICTIONARY,
						MorphDictionaryHolder.class);
				bindExternalResource(colReaderDesc, RESOURCE_KEY_MORPH_DICTIONARY, morphDictDesc);
			}
		}
		// 
		AnalysisEngineDescription xmiWriterDesc = XmiWriter.createDescription(xmiOutputDir, true);
		// make NonTokenizedSpanAnnotator
		AnalysisEngineDescription ntsAnnotatorDesc;
		{
			TypeSystemDescription tsDesc = TypeSystemDescriptionFactory
					.createTypeSystemDescription("ru.ksu.niimm.cll.uima.morph.util.ts-util");
			ntsAnnotatorDesc = createPrimitiveDescription(NonTokenizedSpanAnnotator.class, tsDesc);
		}
		// TODO can we use tokenizer through TokenizerAPI ?
		// make InitialTokenizer for NonTokenizedSpans
		AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
				InitialTokenizer.class,
				TokenizerAPI.PARAM_SPAN_TYPE, NonTokenizedSpan.class.getName());
		// make AnnotationRemovers
		AnalysisEngineDescription scaffoldRemover = createPrimitiveDescription(
				AnnotationRemover.class,
				AnnotationRemover.PARAM_NAMESPACES_TO_REMOVE,
				new String[] { "ru.ksu.niimm.cll.uima.morph.util" });
		//
		SimplePipeline.runPipeline(colReaderDesc,
				ntsAnnotatorDesc, tokenizerDesc, scaffoldRemover, xmiWriterDesc);
	}
}