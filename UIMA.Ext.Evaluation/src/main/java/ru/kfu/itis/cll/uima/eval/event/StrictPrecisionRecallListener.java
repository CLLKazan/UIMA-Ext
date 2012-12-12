/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.SortedSet;

import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class StrictPrecisionRecallListener implements EvaluationListener {

	// config
	private String targetTypeName;
	// derived
	private PrintWriter printer;

	// state fields
	private RecognitionMeasures measures;

	public StrictPrecisionRecallListener(Writer writer) {
		this(null, writer);
	}

	public StrictPrecisionRecallListener(String targetTypeName, Writer writer) {
		this();
		this.targetTypeName = targetTypeName;
		this.printer = new PrintWriter(writer, true);
	}

	private StrictPrecisionRecallListener() {
		this.measures = new RecognitionMeasures();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMissing(String docUri, Type type, Annotation goldAnno) {
		if (!checkType(type)) {
			return;
		}
		measures.incrementMissing(1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMatching(String docUri, Type type, SortedSet<Annotation> goldAnnos,
			SortedSet<Annotation> sysAnnos) {
		if (!checkType(type)) {
			return;
		}
		int matchedNum = 0;
		SortedSet<Annotation> handledSysAnnos = Sets.newTreeSet(sysAnnos.comparator());
		for (Annotation gAnno : goldAnnos) {
			if (sysAnnos.contains(gAnno)) {
				// matched
				measures.incrementMatching(1);
				handledSysAnnos.add(gAnno);
				matchedNum++;
			} else {
				// missed
				measures.incrementMissing(1);
			}
		}
		int spuriousNum = Sets.difference(sysAnnos, handledSysAnnos).size();
		if (spuriousNum != sysAnnos.size() - matchedNum) {
			throw new IllegalStateException("Assertion failed");
		}
		measures.incrementSpurious(spuriousNum);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSpurious(String docUri, Type type, Annotation sysAnno) {
		if (!checkType(type)) {
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
				targetTypeName == null ? "Overall" : targetTypeName,
				measures.getMatchedScore(),
				measures.getMissedScore(),
				measures.getSpuriousScore(),
				measures.getPrecision() * 100,
				measures.getRecall() * 100,
				measures.getF1() * 100);
		printer.println(report);
	}

	public RecognitionMeasures getMeasures() {
		return measures;
	}

	private boolean checkType(Type type) {
		if (targetTypeName == null) {
			return true;
		}
		return type.getName().equals(targetTypeName);
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