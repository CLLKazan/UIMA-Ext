/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.text.AnnotationFS;

import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class StrictPrecisionRecallListener extends TypedPrintingEvaluationListener {

	// state fields
	private RecognitionMeasures measures = new RecognitionMeasures();

	@Override
	@PostConstruct
	protected void init() throws Exception {
		super.init();
	}

	@Override
	public void onDocumentChange(String docUri) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMissing(AnnotationFS goldAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		measures.incrementMissing(1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExactMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		measures.incrementMatching(1);
	}

	@Override
	public void onPartialMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSpurious(AnnotationFS sysAnno) {
		if (!checkType(sysAnno)) {
			return;
		}
		measures.incrementSpurious(1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEvaluationComplete() {
		String report = String.format(REPORT_FMT,
				targetType == null ? "Overall" : targetType.getName(),
				measures.getMatchedScore(),
				measures.getMissedScore(),
				measures.getSpuriousScore(),
				measures.getPrecision() * 100,
				measures.getRecall() * 100,
				measures.getF1() * 100);
		printer.println(report);
		clean();
	}

	public RecognitionMeasures getMeasures() {
		return measures;
	}

	private boolean checkType(AnnotationFS anno) {
		if (targetType == null) {
			return true;
		}
		return ts.subsumes(targetType, anno.getType());
	}

	private static final String PATH_REPORT_FMT = "ru/kfu/itis/cll/uima/eval/event/strict-pr-listener-report.fmt";
	private static final String REPORT_FMT;

	static {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream fmtIS = cl.getResourceAsStream(PATH_REPORT_FMT);
		if (fmtIS == null) {
			throw new IllegalStateException("Can't load reporting format file");
		}
		try {
			REPORT_FMT = IOUtils.toString(fmtIS, "utf-8");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			closeQuietly(fmtIS);
		}
	}
}