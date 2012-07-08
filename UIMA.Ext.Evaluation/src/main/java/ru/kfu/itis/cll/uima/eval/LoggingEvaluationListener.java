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
	public void onMissing(Type type, Annotation goldAnno) {
		print("%s - Missing: text='%s', offset=%s",
				type.getShortName(), goldAnno.getCoveredText(), goldAnno.getBegin());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMatching(Type type, SortedSet<Annotation> goldAnnos,
			SortedSet<Annotation> sysAnnos) {
		if (goldAnnos.size() == 1 && sysAnnos.size() == 1) {
			Annotation goldAnno = goldAnnos.iterator().next();
			Annotation sysAnno = sysAnnos.iterator().next();
			if (goldAnno.getBegin() == sysAnno.getBegin()
					&& goldAnno.getEnd() == sysAnno.getEnd()) {
				print("%s - Exact match: text='%s', offset=%s",
						type.getShortName(), sysAnno.getCoveredText(), sysAnno.getBegin());
				return;
			}
		}
		print("%s - Partial match:\nGold: %s\nSystem: %s",
				type.getShortName(),
				Joiner.on(", ").join(transform(goldAnnos, annoToPrinter)),
				Joiner.on(", ").join(transform(sysAnnos, annoToPrinter)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSpurious(Type type, Annotation sysAnno) {
		print("%s - Spurious: text='%s', offset=%s",
				type.getShortName(), sysAnno.getCoveredText(), sysAnno.getBegin());
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