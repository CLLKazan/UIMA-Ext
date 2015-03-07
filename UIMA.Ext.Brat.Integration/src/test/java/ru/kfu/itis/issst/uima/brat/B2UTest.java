package ru.kfu.itis.issst.uima.brat;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;

import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class B2UTest {

	private static final String inputBratDir = "data/news.brat";
	private static final String B2UAggregateDesc = "desc/aggregates/b2u-test-aggregate.xml";

	@Test
	public void testReverse() throws Exception {
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setMaxProcessingUnitThreatCount(2);

		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("desc.types.test-TypeSystem");

		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createReaderDescription(
				BratCollectionReader.class, tsd,
				BratCollectionReader.PARAM_BRAT_COLLECTION_DIR, inputBratDir,
				BratCollectionReader.PARAM_MAPPING_FACTORY_CLASS,
				ReverseBratUimaMappingFactory.class.getName(),
				ReverseBratUimaMappingFactory.PARAM_U2B_DESC_PATH, U2BTest.U2BAggregateDesc);

		cpeBuilder.setReader(colReaderDesc);
		// configure AE
		XMLInputSource aeDescInput = new XMLInputSource(B2UAggregateDesc);
		AnalysisEngineDescription aeDesc = UIMAFramework.getXMLParser()
				.parseAnalysisEngineDescription(aeDescInput);
		cpeBuilder.addAnalysisEngine(aeDesc);

		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe));
		TestStatusCallbackListener testCallback = new TestStatusCallbackListener();
		cpe.addStatusCallbackListener(testCallback);
		cpe.process();

		while (!testCallback.aborted && !testCallback.completed) {
			Thread.sleep(1000);
		}
		if (testCallback.aborted) {
			fail("CPE aborted");
		} else if (!testCallback.completed) {
			fail("CPE was not aborted but it is not completed as well O_o");
		}
	}

	@Test
	public void testAuto() throws Exception {
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setMaxProcessingUnitThreatCount(2);

		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("desc.types.brat-news-tutorial-TypeSystem");

		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createReaderDescription(
				BratCollectionReader.class, tsd,
				BratCollectionReader.PARAM_BRAT_COLLECTION_DIR, "data/brat-news-tutorial",
				BratCollectionReader.PARAM_MAPPING_FACTORY_CLASS,
				AutoBratUimaMappingFactory.class.getName(),
				AutoBratUimaMappingFactory.PARAM_NAMESPACES_TO_SCAN, "ace");

		cpeBuilder.setReader(colReaderDesc);
		// configure AE
		AnalysisEngineDescription aeDesc = createEngineDescription(XmiWriter.class,
				XmiWriter.PARAM_OUTPUTDIR, "target/brat-news-tutorial.xmi");
		cpeBuilder.addAnalysisEngine(aeDesc);

		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe));
		TestStatusCallbackListener testCallback = new TestStatusCallbackListener();
		cpe.addStatusCallbackListener(testCallback);
		cpe.process();

		while (!testCallback.aborted && !testCallback.completed) {
			Thread.sleep(1000);
		}
		if (testCallback.aborted) {
			fail("CPE aborted");
		} else if (!testCallback.completed) {
			fail("CPE was not aborted but it is not completed as well O_o");
		}
	}
}