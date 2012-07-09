/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static com.google.common.collect.Collections2.transform;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.SortedSet;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LoggingEvaluationListener implements EvaluationListener {

	// derived
	private PrintWriter printer;

	public LoggingEvaluationListener(Writer writer) {
		printer = new PrintWriter(writer, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMissing(String docUri, Type type, Annotation goldAnno) {
		print("%s - Missing: text='%s', docUri=%s offset=%s",
				type.getShortName(), goldAnno.getCoveredText(),
				docUri, goldAnno.getBegin());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMatching(String docUri, Type type, SortedSet<Annotation> goldAnnos,
			SortedSet<Annotation> sysAnnos) {
		if (goldAnnos.size() == 1 && sysAnnos.size() == 1) {
			Annotation goldAnno = goldAnnos.iterator().next();
			Annotation sysAnno = sysAnnos.iterator().next();
			if (goldAnno.getBegin() == sysAnno.getBegin()
					&& goldAnno.getEnd() == sysAnno.getEnd()) {
				print("%s - Exact match: text='%s', doc=%s, offset=%s",
						type.getShortName(), sysAnno.getCoveredText(),
						docUri, sysAnno.getBegin());
				return;
			}
		}
		print("%s - Partial match in %s:\nGold: %s\nSystem: %s",
				type.getShortName(),
				docUri,
				Joiner.on(", ").join(transform(goldAnnos, annoToPrinter)),
				Joiner.on(", ").join(transform(sysAnnos, annoToPrinter)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSpurious(String docUri, Type type, Annotation sysAnno) {
		print("%s - Spurious: text='%s', doc=%s, offset=%s",
				type.getShortName(), sysAnno.getCoveredText(),
				docUri, sysAnno.getBegin());
	}

	private void print(String msg, Object... args) {
		printer.println(String.format(msg, args));
	}

	private final AnnoToPrinter annoToPrinter = new AnnoToPrinter();

	private class AnnoToPrinter implements Function<Annotation, AnnotationPrinter> {
		@Override
		public AnnotationPrinter apply(Annotation input) {
			return new AnnotationPrinter(input);
		}
	}

	private class AnnotationPrinter {
		private Annotation anno;

		public AnnotationPrinter(Annotation anno) {
			this.anno = anno;
		}

		@Override
		public String toString() {
			return String.format("('%s',%s)",
					anno.getCoveredText(), anno.getBegin());
		}
	}
}