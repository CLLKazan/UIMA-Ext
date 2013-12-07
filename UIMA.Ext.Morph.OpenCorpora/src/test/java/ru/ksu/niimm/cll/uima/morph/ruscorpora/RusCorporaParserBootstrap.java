/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.opencorpora.cas.Word;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
import ru.kfu.itis.cll.uima.annotator.AnnotationRemover;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.StatusCallbackListenerAdapter;
import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.ksu.niimm.cll.uima.morph.util.NonTokenizedSpan;
import ru.ksu.niimm.cll.uima.morph.util.NonTokenizedSpanAnnotator;

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

	private RusCorporaParserBootstrap() {
	}

	private void run() throws Exception {
		CollectionReaderDescription colReaderDesc;
		{
			TypeSystemDescription tsDesc = TypeSystemDescriptionFactory
					.createTypeSystemDescription(
							"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
							"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
							"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
							"org.opencorpora.morphology-ts");
			//
			colReaderDesc = CollectionReaderFactory.createDescription(
					RusCorporaCollectionReader.class,
					tsDesc,
					RusCorporaCollectionReader.PARAM_INPUT_DIR, ruscorporaTextDir.getPath());
			//RusCorporaCollectionReader.PARAM_TAG_MAPPER_CLASS, IdentityTagTagger.class.getName()
		}
		// 
		AnalysisEngineDescription xmiWriterDesc = createPrimitiveDescription(
				XmiWriter.class,
				XmiWriter.PARAM_OUTPUTDIR, xmiOutputDir);
		// make NonTokenizedSpanAnnotator
		AnalysisEngineDescription ntsAnnotatorDesc;
		{
			TypeSystemDescription tsDesc = TypeSystemDescriptionFactory
					.createTypeSystemDescription("ru.ksu.niimm.cll.uima.morph.util.ts-util");
			ntsAnnotatorDesc = createPrimitiveDescription(NonTokenizedSpanAnnotator.class, tsDesc);
		}
		// make InitialTokenizer for NonTokenizedSpans
		AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
				InitialTokenizer.class,
				InitialTokenizer.PARAM_SPAN_TYPE, NonTokenizedSpan.class.getName());
		// make AnnotationRemovers
		AnalysisEngineDescription scaffoldRemover = createPrimitiveDescription(
				AnnotationRemover.class,
				AnnotationRemover.PARAM_NAMESPACES_TO_REMOVE,
				new String[] { "ru.ksu.niimm.cll.uima.morph.util" });
		// make AGGREGATE
		AnalysisEngineDescription aggregateDesc = AnalysisEngineFactory.createAggregateDescription(
				ntsAnnotatorDesc, tokenizerDesc, scaffoldRemover, xmiWriterDesc);
		//
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setReader(colReaderDesc);
		cpeBuilder.addAnalysisEngine(aggregateDesc);
		cpeBuilder.setMaxProcessingUnitThreatCount(3);
		final CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		//
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(
				cpe, 50));
		cpe.addStatusCallbackListener(new WordCountingListener());
		cpe.process();
	}

	private static class WordCountingListener extends StatusCallbackListenerAdapter {

		private AtomicInteger wordCounter = new AtomicInteger(0);

		@Override
		public void collectionProcessComplete() {
			System.out.println(String.format("%s words have been parsed", wordCounter));
		}

		@Override
		public void entityProcessComplete(CAS cas, EntityProcessStatus aStatus) {
			Type wordType = cas.getTypeSystem().getType(Word.class.getName());
			wordCounter.addAndGet(cas.getAnnotationIndex(wordType).size());
		}

	}
}