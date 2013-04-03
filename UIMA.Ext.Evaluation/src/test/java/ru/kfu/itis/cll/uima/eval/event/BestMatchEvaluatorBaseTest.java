/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import static org.junit.Assert.*;

import ru.kfu.itis.cll.uima.eval.TypeSystemInitializer;
import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@ContextConfiguration(classes = BestMatchEvaluatorBaseTest.AppContext.class)
public class BestMatchEvaluatorBaseTest extends AbstractJUnit4SpringContextTests {

	@PropertySource("classpath:BestMatchEvaluatorBaseTest.properties")
	@Configuration
	public static class AppContext {
		@Bean
		public BestMatchEvaluatorBase evaluator() {
			return new BestMatchEvaluatorBase() {
				@Override
				protected RecognitionMeasures evaluateAnno(AnnotationFS goldAnno,
						AnnotationFS sysAnno) {
					RecognitionMeasures result = new RecognitionMeasures();
					int overlapBegin = Math.max(goldAnno.getBegin(), sysAnno.getBegin());
					int overlapEnd = Math.min(goldAnno.getEnd(), sysAnno.getEnd());
					if (overlapBegin >= overlapEnd) {
						// annotations do not overlap
						result.incrementMissing(1);
						result.incrementSpurious(1);
					} else {
						result.incrementMatching(overlapEnd - overlapBegin);
						// calc missing
						if (goldAnno.getBegin() < overlapBegin) {
							result.incrementMissing(overlapBegin - goldAnno.getBegin());
						}
						if (goldAnno.getEnd() > overlapEnd) {
							result.incrementMissing(goldAnno.getEnd() - overlapEnd);
						}
						// calc spurious
						if (sysAnno.getBegin() < overlapBegin) {
							result.incrementSpurious(overlapBegin - sysAnno.getBegin());
						}
						if (sysAnno.getEnd() > overlapEnd) {
							result.incrementSpurious(sysAnno.getEnd() - overlapEnd);
						}
					}
					return result;
				}
			};
		}

		@Bean
		public TypeSystemInitializer typeSystemInitializer() {
			return new TypeSystemInitializer();
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer pspc() {
			return new PropertySourcesPlaceholderConfigurer();
		}
	}

	@Autowired
	private TypeSystem ts;
	@Autowired
	private BestMatchEvaluatorBase evaluator;

	@Test
	@DirtiesContext
	public void test1() throws UIMAException {
		Type t1 = ts.getType("test.TestFirst");
		CAS cas = CasCreationUtils.createCas(ts, null, null, null);
		cas.setDocumentText(TXT);
		AnnotationFS s1 = cas.createAnnotation(t1, 0, 10);
		AnnotationFS g1 = cas.createAnnotation(t1, 0, 9);
		evaluator.onDocumentChange("1");
		evaluator.onPartialMatch(g1, s1);
		evaluator.onDocumentChange(null);
		RecognitionMeasures m = evaluator.getMeasures();
		assertEquals(0.9f, m.getMatchedScore(), 0.01f);
		assertEquals(0.1f, m.getSpuriousScore(), 0.01f);
		assertEquals(0f, m.getMissedScore(), 0.01f);
	}

	@Test
	@DirtiesContext
	public void test2() throws UIMAException {
		Type t1 = ts.getType("test.TestFirst");
		CAS cas = CasCreationUtils.createCas(ts, null, null, null);
		cas.setDocumentText(TXT);
		AnnotationFS g1 = cas.createAnnotation(t1, 0, 9);
		AnnotationFS s1 = cas.createAnnotation(t1, 2, 3);
		AnnotationFS s2 = cas.createAnnotation(t1, 0, 18);
		AnnotationFS s3 = cas.createAnnotation(t1, 6, 12);
		evaluator.onDocumentChange("1");
		evaluator.onPartialMatch(g1, s1);
		evaluator.onPartialMatch(g1, s2);
		evaluator.onPartialMatch(g1, s3);
		evaluator.onMissing(g1);
		evaluator.onDocumentChange(null);
		RecognitionMeasures m = evaluator.getMeasures();
		assertEquals(0.5f, m.getMatchedScore(), 0.01f);
		assertEquals(2.5f, m.getSpuriousScore(), 0.01f);
		assertEquals(0f, m.getMissedScore(), 0.01f);
	}

	@Test
	@DirtiesContext
	public void test3() throws UIMAException {
		// the same as test2 but with exactMatch invoked
		Type t1 = ts.getType("test.TestFirst");
		CAS cas = CasCreationUtils.createCas(ts, null, null, null);
		cas.setDocumentText(TXT);
		AnnotationFS g1 = cas.createAnnotation(t1, 0, 9);
		AnnotationFS s1 = cas.createAnnotation(t1, 0, 9);
		AnnotationFS s2 = cas.createAnnotation(t1, 0, 18);
		AnnotationFS s3 = cas.createAnnotation(t1, 6, 12);
		evaluator.onDocumentChange("1");
		evaluator.onExactMatch(g1, s1);
		evaluator.onPartialMatch(g1, s2);
		evaluator.onPartialMatch(g1, s3);
		evaluator.onDocumentChange(null);
		RecognitionMeasures m = evaluator.getMeasures();
		assertEquals(1f, m.getMatchedScore(), 0.01f);
		assertEquals(2f, m.getSpuriousScore(), 0.01f);
		assertEquals(0f, m.getMissedScore(), 0.01f);
	}

	private static final String TXT;
	static {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			sb.append("FOOBAR ");
		}
		TXT = sb.toString();
	}
}