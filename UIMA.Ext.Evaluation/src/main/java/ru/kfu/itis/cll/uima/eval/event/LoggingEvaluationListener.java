/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.text.AnnotationFS;

import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LoggingEvaluationListener extends PrintingEvaluationListener {

	// config
	private boolean stripDocumentUri;

	// state
	private String currentDocUri;
	// collect system annotations that partially match gold ones
	// this is necessary to avoid their duplications as Spurious
	private Set<AnnotationFS> partiallyMatched;

	public void setStripDocumentUri(boolean stripDocumentUri) {
		this.stripDocumentUri = stripDocumentUri;
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
				goldAnno.getCoveredText(), String.valueOf(goldAnno.getBegin()),
				null, null, currentDocUri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExactMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		printRow(goldAnno.getType().getShortName(), "Exact",
				goldAnno.getCoveredText(), String.valueOf(goldAnno.getBegin()),
				sysAnno.getCoveredText(), String.valueOf(sysAnno.getBegin()),
				currentDocUri);
	}

	@Override
	public void onPartialMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		partiallyMatched.add(sysAnno);
		printRow(goldAnno.getType().getShortName(), "Partial",
				goldAnno.getCoveredText(), String.valueOf(goldAnno.getBegin()),
				sysAnno.getCoveredText(), String.valueOf(sysAnno.getBegin()),
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
					sysAnno.getCoveredText(), String.valueOf(sysAnno.getBegin()),
					currentDocUri);
		}
	}

	@Override
	public void onEvaluationComplete() {
		partiallyMatched = null;

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
}