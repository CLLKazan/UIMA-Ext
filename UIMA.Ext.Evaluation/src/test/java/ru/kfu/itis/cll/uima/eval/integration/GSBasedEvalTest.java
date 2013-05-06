/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.integration;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import ru.kfu.itis.cll.uima.eval.GoldStandardBasedEvaluation;
import ru.kfu.itis.cll.uima.eval.event.EvaluationListener;
import ru.kfu.itis.cll.uima.eval.event.LoggingEvaluationListener;
import ru.kfu.itis.cll.uima.eval.event.SoftPrecisionRecallListener;
import ru.kfu.itis.cll.uima.eval.event.StrictPrecisionRecallListener;
import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

/**
 * @author Rinat Gareev
 * 
 */
@ContextConfiguration(classes = GSBasedEvalTest.AppContext.class)
public class GSBasedEvalTest extends AbstractJUnit4SpringContextTests {

	@PropertySource("classpath:GSBasedEvalTest.properties")
	@Configuration
	@ImportResource("classpath:ru/kfu/itis/cll/uima/eval/app-context.xml")
	public static class AppContext {
		@Bean
		public EvaluationListener softEvalListener() {
			SoftPrecisionRecallListener listener = new SoftPrecisionRecallListener();
			listener.setTargetTypeName("test.TestFirst");
			return listener;
		}

		@Bean
		public EvaluationListener strictEvalListener1() {
			StrictPrecisionRecallListener listener = new StrictPrecisionRecallListener();
			listener.setTargetTypeName("test.TestFirst");
			return listener;
		}

		@Bean
		public EvaluationListener strictEvalListener2() {
			StrictPrecisionRecallListener listener = new StrictPrecisionRecallListener();
			listener.setTargetTypeName("test.TestSecond");
			return listener;
		}

		@Bean
		public EvaluationListener strictEvalListenerOverall() {
			return new StrictPrecisionRecallListener();
		}

		@Bean
		public EvaluationListener loggingListener() {
			LoggingEvaluationListener list = new LoggingEvaluationListener();
			list.setStripDocumentUri(true);
			return list;
		}
	}

	@Autowired
	private GoldStandardBasedEvaluation evaluator;
	@Resource(name = "softEvalListener")
	private SoftPrecisionRecallListener softEvalListener;
	@Resource(name = "strictEvalListener1")
	private StrictPrecisionRecallListener strictEvalListener1;
	@Resource(name = "strictEvalListener2")
	private StrictPrecisionRecallListener strictEvalListener2;
	@Resource(name = "strictEvalListenerOverall")
	private StrictPrecisionRecallListener strictEvalListenerOverall;

	@Test
	@DirtiesContext
	public void test() throws Exception {
		evaluator.run();

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
			assertEquals(3f, strictMeasures.getSpuriousScore(), 0.001f);
			assertEquals(1f, strictMeasures.getMissedScore(), 0.001f);

			assertEquals(0.4f, strictMeasures.getPrecision(), 0.01f);
			assertEquals(0.67f, strictMeasures.getRecall(), 0.01f);
			assertEquals(0.501f, strictMeasures.getF1(), 0.001f);
		}

		{
			RecognitionMeasures strictMeasures = strictEvalListenerOverall.getMeasures();
			assertEquals(4f, strictMeasures.getMatchedScore(), 0.001f);
			assertEquals(10f, strictMeasures.getSpuriousScore(), 0.001f);
			assertEquals(8f, strictMeasures.getMissedScore(), 0.001f);

			assertEquals(0.286f, strictMeasures.getPrecision(), 0.001f);
			assertEquals(0.333f, strictMeasures.getRecall(), 0.001f);
			assertEquals(0.308f, strictMeasures.getF1(), 0.001f);
		}
	}
}