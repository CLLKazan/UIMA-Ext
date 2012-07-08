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
import ru.kfu.itis.cll.uima.eval.LoggingEvaluationListener;
import ru.kfu.itis.cll.uima.eval.SoftPrecisionRecallListener;

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

		cfg.setGoldStandardImpl("ru.kfu.itis.cll.uima.eval.FSCasDirectory");
		Map<String, String> goldCDProps = Maps.newHashMap();
		goldCDProps.put("dir", "data/test-gold");
		cfg.setGoldStandardProps(goldCDProps);

		cfg.setSystemOutputImpl("ru.kfu.itis.cll.uima.eval.FSCasDirectory");
		Map<String, String> sysCDProps = Maps.newHashMap();
		sysCDProps.put("dir", "data/test-sysout");
		cfg.setSystemOutputProps(sysCDProps);

		cfg.setTypeSystemDescPath("desc/types/aggregate-4Runtime-TS.xml");

		EvaluationContext evalCtx = new EvaluationContext();
		SoftPrecisionRecallListener metrics = new SoftPrecisionRecallListener("test.TestFirst");
		evalCtx.addListener(metrics);
		LoggingEvaluationListener loggingListener = new LoggingEvaluationListener(
				new OutputStreamWriter(System.out));
		evalCtx.addListener(loggingListener);

		new GoldStandardBasedEvaluation(cfg).run(evalCtx);

		assertEquals(3.048f, metrics.getMatchedScore(), 0.001f);
		assertEquals(3.571f, metrics.getSpuriousScore(), 0.001f);
		assertEquals(4.381f, metrics.getMissedScore(), 0.001f);

		assertEquals(0.46f, metrics.getPrecision(), 0.01f);
		assertEquals(0.41f, metrics.getRecall(), 0.01f);
		assertEquals(0.434f, metrics.getF1(), 0.001f);
	}

}