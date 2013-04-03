/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.Type;

import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class MeasuresReportUtils {

	static String getReportString(Type targetType, RecognitionMeasures m) {
		return String.format(REPORT_FMT,
				targetType == null ? "Overall" : targetType.getName(),
				m.getMatchedScore(),
				m.getMissedScore(),
				m.getSpuriousScore(),
				m.getPrecision() * 100,
				m.getRecall() * 100,
				m.getF1() * 100);
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

	private MeasuresReportUtils() {
	}
}