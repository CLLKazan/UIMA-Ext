package ru.kfu.itis.issst.uima.brat;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLInputSource;
import org.junit.Test;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;
import ru.kfu.itis.cll.uima.cpe.XmiCollectionReader;

import static org.junit.Assert.*;

public class U2BTest {

	private static final String inputFileXMIDir = "data/news.xmi";
	public static final String U2BAggregateDesc = "desc/aggregates/u2b-test-aggregate.xml";

	@Test
	public void test() throws Exception {
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setMaxProcessingUnitThreatCount(1);

		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescription("desc.types.test-TypeSystem");

		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createDescription(
				XmiCollectionReader.class, tsd,
				XmiCollectionReader.PARAM_INPUTDIR, inputFileXMIDir);

		cpeBuilder.setReader(colReaderDesc);
		// configure AE
		XMLInputSource aeDescInput = new XMLInputSource(U2BAggregateDesc);
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
}