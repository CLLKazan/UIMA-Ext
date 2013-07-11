/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static java.lang.System.exit;
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
		if (args.length != 3) {
			System.err
					.println("Usage: <ruscorpora-text-dir> <output-xmi-dir> <output-premorph-xmi-dir>");
			exit(1);
		}
		File ruscorporaTextDir = new File(args[0]);
		if (!ruscorporaTextDir.isDirectory()) {
			System.err.println(String.format("%s is not existing directory", ruscorporaTextDir));
			exit(1);
		}
		String xmiOutput1Dir = args[1];
		String xmiOutput2Dir = args[2];
		// setup logging
		Slf4jLoggerImpl.forceUsingThisImplementation();
		//
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
		AnalysisEngineDescription xmiWriter1Desc = createPrimitiveDescription(
				XmiWriter.class,
				XmiWriter.PARAM_OUTPUTDIR, xmiOutput1Dir);
		AnalysisEngineDescription xmiWriter2Desc = createPrimitiveDescription(
				XmiWriter.class,
				XmiWriter.PARAM_OUTPUTDIR, xmiOutput2Dir);
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
		AnalysisEngineDescription morphRemover = createPrimitiveDescription(
				AnnotationRemover.class,
				AnnotationRemover.PARAM_NAMESPACES_TO_REMOVE,
				new String[] { "org.opencorpora.cas" });
		// make AGGREGATE
		AnalysisEngineDescription aggregateDesc = AnalysisEngineFactory.createAggregateDescription(
				ntsAnnotatorDesc, tokenizerDesc, scaffoldRemover, xmiWriter1Desc,
				morphRemover, xmiWriter2Desc);
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