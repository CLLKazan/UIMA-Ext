/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.integration;

import static junit.framework.Assert.assertEquals;

import java.io.OutputStreamWriter;
import java.util.Map;

import org.junit.Test;

import ru.kfu.itis.cll.uima.eval.EvaluationConfig;
import ru.kfu.itis.cll.uima.eval.EvaluationContext;
import ru.kfu.itis.cll.uima.eval.GoldStandardBasedEvaluation;
import ru.kfu.itis.cll.uima.eval.event.LoggingEvaluationListener;
import ru.kfu.itis.cll.uima.eval.event.SoftPrecisionRecallListener;
import ru.kfu.itis.cll.uima.eval.event.StrictPrecisionRecallListener;
import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev
 * 
 */
public class GSBasedEvalTest {

	@Test
	public void test() throws Exception {
		EvaluationConfig cfg = new EvaluationConfig();
		cfg.setAnnoTypes(Sets.newHashSet("test.TestFirst", "test.TestSecond"));
		cfg.setDocUriAnnotationType("ru.kfu.itis.cll.uima.commons.DocumentMetadata");
		cfg.setDocUriFeatureName("sourceUri");

		cfg.setGoldStandardImpl("ru.kfu.itis.cll.uima.eval.cas.FSCasDirectory");
		Map<String, String> goldCDProps = Maps.newHashMap();
		goldCDProps.put("dir", "data/test-gold");
		cfg.setGoldStandardProps(goldCDProps);

		cfg.setSystemOutputImpl("ru.kfu.itis.cll.uima.eval.cas.FSCasDirectory");
		Map<String, String> sysCDProps = Maps.newHashMap();
		sysCDProps.put("dir", "data/test-sysout");
		cfg.setSystemOutputProps(sysCDProps);

		cfg.setTypeSystemDescPaths(new String[] { "desc/types/aggregate-4Runtime-TS.xml" });

		OutputStreamWriter outWriter = new OutputStreamWriter(System.out);

		EvaluationContext evalCtx = new EvaluationContext();
		SoftPrecisionRecallListener softEvalListener = new SoftPrecisionRecallListener(
				"test.TestFirst",
				outWriter);
		evalCtx.addListener(softEvalListener);

		StrictPrecisionRecallListener strictEvalListener1 = new StrictPrecisionRecallListener(
				"test.TestFirst",
				outWriter);
		evalCtx.addListener(strictEvalListener1);

		StrictPrecisionRecallListener strictEvalListener2 = new StrictPrecisionRecallListener(
				"test.TestSecond",
				outWriter);
		evalCtx.addListener(strictEvalListener2);

		StrictPrecisionRecallListener strictEvalListenerOverall = new StrictPrecisionRecallListener(
				outWriter);
		evalCtx.addListener(strictEvalListenerOverall);

		LoggingEvaluationListener loggingListener = new LoggingEvaluationListener(outWriter);
		evalCtx.addListener(loggingListener);

		new GoldStandardBasedEvaluation(cfg).run(evalCtx);

		{
			RecognitionMeasures softMetrics = softEvalListener.getMeasures();
			assertEquals(3.048f, softMetrics.getMatchedScore(), 0.001f);
			assertEquals(4.571f, softMetrics.getSpuriousScore(), 0.001f);
			assertEquals(4.381f, softMetrics.getMissedScore(), 0.001f);

			assertEquals(0.40f, softMetrics.getPrecision(), 0.01f);
			assertEquals(0.41f, softMetrics.getRecall(), 0.01f);
			assertEquals(0.405f, softMetrics.getF1(), 0.001f);
		}

		{
			RecognitionMeasures strictMeasures = strictEvalListener1.getMeasures();
			assertEquals(2f, strictMeasures.getMatchedScore(), 0.001f);
			assertEquals(7f, strictMeasures.getSpuriousScore(), 0.001f);
			assertEquals(7f, strictMeasures.getMissedScore(), 0.001f);

			assertEquals(0.22f, strictMeasures.getPrecision(), 0.01f);
			assertEquals(0.22f, strictMeasures.getRecall(), 0.01f);
			assertEquals(0.222f, strictMeasures.getF1(), 0.001f);
		}
		
		{
			RecognitionMeasures strictMeasures = strictEvalListener2.getMeasures();
			assertEquals(2f, strictMeasures.getMatchedScore(), 0.001f);
			assertEquals(2f, strictMeasures.getSpuriousScore(), 0.001f);
			assertEquals(1f, strictMeasures.getMissedScore(), 0.001f);

			assertEquals(0.5f, strictMeasures.getPrecision(), 0.01f);
			assertEquals(0.67f, strictMeasures.getRecall(), 0.01f);
			assertEquals(0.571f, strictMeasures.getF1(), 0.001f);
		}
		
		{
			RecognitionMeasures strictMeasures = strictEvalListenerOverall.getMeasures();
			assertEquals(4f, strictMeasures.getMatchedScore(), 0.001f);
			assertEquals(9f, strictMeasures.getSpuriousScore(), 0.001f);
			assertEquals(8f, strictMeasures.getMissedScore(), 0.001f);

			assertEquals(0.31f, strictMeasures.getPrecision(), 0.01f);
			assertEquals(0.33f, strictMeasures.getRecall(), 0.01f);
			assertEquals(0.320f, strictMeasures.getF1(), 0.001f);
		}
	}
}