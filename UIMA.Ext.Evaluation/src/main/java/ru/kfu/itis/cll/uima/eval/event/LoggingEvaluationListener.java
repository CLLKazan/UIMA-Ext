/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.beans.factory.annotation.Autowired;

import ru.kfu.itis.cll.uima.eval.event.logging.AnnotationPrinter;
import ru.kfu.itis.cll.uima.eval.matching.Matcher;

import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LoggingEvaluationListener extends PrintingEvaluationListener {

	@Autowired
	private TypeSystem ts;
	@Autowired
	private Matcher<AnnotationFS> matcher;

	// config
	private boolean stripDocumentUri;
	private Class<? extends AnnotationPrinter> annoPrinterClass;

	// derived
	private AnnotationPrinter annoPrinter;
	// state fields
	//
	// collect system annotations that partially match gold ones
	// this is necessary to avoid their duplications as Spurious
	private Set<AnnotationFS> partiallyMatched;

	@PostConstruct
	@Override
	protected void init() throws Exception {
		super.init();
		if (annoPrinterClass == null) {
			annoPrinter = new MatcherPrinter();
		} else {
			annoPrinter = annoPrinterClass.newInstance();
			annoPrinter.init(ts);
		}
	}

	public void setStripDocumentUri(boolean stripDocumentUri) {
		this.stripDocumentUri = stripDocumentUri;
	}

	public void setAnnotationPrinterClass(Class<? extends AnnotationPrinter> annoPrinterClass) {
		this.annoPrinterClass = annoPrinterClass;
	}

	@Override
	public void onDocumentChange(String docUri) {
		this.currentDocUri = prepareUri(docUri);
		partiallyMatched = Sets.newHashSet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onMissing(AnnotationFS goldAnno) {
		printRow(goldAnno.getType().getShortName(), "Missing",
				annoPrinter.getString(goldAnno), String.valueOf(goldAnno.getBegin()),
				null, null, currentDocUri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExactMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		printRow(goldAnno.getType().getShortName(), "Exact",
				annoPrinter.getString(goldAnno), String.valueOf(goldAnno.getBegin()),
				annoPrinter.getString(sysAnno), String.valueOf(sysAnno.getBegin()),
				currentDocUri);
	}

	@Override
	public void onPartialMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		partiallyMatched.add(sysAnno);
		printRow(goldAnno.getType().getShortName(), "Partial",
				annoPrinter.getString(goldAnno), String.valueOf(goldAnno.getBegin()),
				annoPrinter.getString(sysAnno), String.valueOf(sysAnno.getBegin()),
				currentDocUri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSpurious(AnnotationFS sysAnno) {
		if (!partiallyMatched.contains(sysAnno)) {
			printRow(sysAnno.getType().getShortName(), "Spurious",
					null, null,
					annoPrinter.getString(sysAnno), String.valueOf(sysAnno.getBegin()),
					currentDocUri);
		}
	}

	@Override
	public void onEvaluationComplete() {
		partiallyMatched = null;
		clean();
	}

	private String prepareUri(String srcUri) {
		if (!stripDocumentUri || srcUri == null) {
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

	private class MatcherPrinter implements AnnotationPrinter {

		@Override
		public void init(TypeSystem ts) {
		}

		@Override
		public String getString(AnnotationFS anno) {
			StringBuilder sb = new StringBuilder();
			matcher.print(sb, anno);
			return sb.toString();
		}
	}
}