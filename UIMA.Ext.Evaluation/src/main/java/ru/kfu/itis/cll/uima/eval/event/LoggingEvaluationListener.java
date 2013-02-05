/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import static com.google.common.collect.Collections2.transform;

import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.SortedSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.base.Function;
import com.google.common.base.Joiner;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LoggingEvaluationListener implements EvaluationListener {

	private boolean stripDocumentUri;
	// derived
	private PrintWriter printer;

	public LoggingEvaluationListener(Writer writer, boolean stripDocumentUri) {
		this.stripDocumentUri = stripDocumentUri;
		printer = new PrintWriter(writer, true);
	}

	public LoggingEvaluationListener(Writer writer) {
		this(writer, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMissing(String docUri, Type type, Annotation goldAnno) {
		docUri = prepareUri(docUri);
		printRow(type.getShortName(), "Missing",
				goldAnno.getCoveredText(), String.valueOf(goldAnno.getBegin()),
				null, null, docUri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMatching(String docUri, Type type, SortedSet<Annotation> goldAnnos,
			SortedSet<Annotation> sysAnnos) {
		docUri = prepareUri(docUri);
		if (goldAnnos.size() == 1 && sysAnnos.size() == 1) {
			Annotation goldAnno = goldAnnos.iterator().next();
			Annotation sysAnno = sysAnnos.iterator().next();
			if (goldAnno.getBegin() == sysAnno.getBegin()
					&& goldAnno.getEnd() == sysAnno.getEnd()) {
				printRow(type.getShortName(), "Exact",
						goldAnno.getCoveredText(), String.valueOf(goldAnno.getBegin()),
						sysAnno.getCoveredText(), String.valueOf(sysAnno.getBegin()),
						docUri);
				return;
			}
		}
		printRow(type.getShortName(), "Partial",
				Joiner.on(" /// ").join(transform(goldAnnos, annoToTxt)),
				Joiner.on(", ").join(transform(goldAnnos, annoToOffset)),
				Joiner.on(" /// ").join(transform(sysAnnos, annoToTxt)),
				Joiner.on(", ").join(transform(sysAnnos, annoToOffset)),
				docUri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSpurious(String docUri, Type type, Annotation sysAnno) {
		docUri = prepareUri(docUri);
		printRow(type.getShortName(), "Spurious",
				null, null,
				sysAnno.getCoveredText(), String.valueOf(sysAnno.getBegin()),
				docUri);
	}

	@Override
	public void onEvaluationComplete() {
	}

	private String prepareUri(String srcUri) {
		if (!stripDocumentUri) {
			return srcUri;
		}
		try {
			URI uri = new URI(srcUri);
			String name = FilenameUtils.getName(uri.getPath());
			if (StringUtils.isBlank(name)) {
				name = srcUri;
			}
			return name;
		} catch (URISyntaxException e) {
			return srcUri;
		}
	}

	@SuppressWarnings("unused")
	private void print(String msg, Object... args) {
		printer.println(String.format(msg, args));
	}

	private void printRow(String annoType, String matchType,
			String goldTxt, String goldOffset,
			String sysTxt, String sysOffset, String docUri) {
		// tab-separated
		if (goldOffset == null) {
			goldOffset = "-1";
		}
		if (sysOffset == null) {
			sysOffset = "-1";
		}
		if (goldTxt == null) {
			goldTxt = "";
		}
		if (sysTxt == null) {
			sysTxt = "";
		}
		StringBuilder sb = new StringBuilder(annoType).append('\t');
		sb.append(matchType).append('\t');
		sb.append(escTab(goldTxt)).append('\t').append(goldOffset).append('\t');
		sb.append(escTab(sysTxt)).append('\t').append(sysOffset).append('\t');
		sb.append(docUri);
		printer.println(sb.toString());
	}

	private String escTab(String src) {
		return StringUtils.replace(src, "\t", "    ");
	}

	private final Function<Annotation, String> annoToTxt = new Function<Annotation, String>() {
		@Override
		public String apply(Annotation input) {
			return input.getCoveredText();
		}
	};

	private final Function<Annotation, Integer> annoToOffset = new Function<Annotation, Integer>() {
		@Override
		public Integer apply(Annotation input) {
			return input.getBegin();
		}
	};
}