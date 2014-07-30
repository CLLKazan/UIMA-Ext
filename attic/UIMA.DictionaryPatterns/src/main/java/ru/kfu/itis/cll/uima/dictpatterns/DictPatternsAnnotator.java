/**
 * 
 */
package ru.kfu.itis.cll.uima.dictpatterns;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import ru.kfu.itis.cll.uima.dictpatterns.core.DictPatternMatch;
import ru.kfu.itis.cll.uima.dictpatterns.core.DictPatternsMatcher;
import ru.kfu.itis.cll.uima.dictpatterns.core.PatternElementSpan;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictPatternsAnnotator extends CasAnnotator_ImplBase {

	private static final String PARAM_PATTERN_FILE_ENCODING = "PatternFileEncoding";
	private static final String RESOURCE_PATTERN_FILE = "PatternFile";
	private static final String PARAM_RESULT_ANNOTATION_TYPE = "ResultAnnotationType";
	private static final String PARAM_SPAN_ANNOTATION_TYPE = "SpanAnnotationType";
	private static final String DEFAULT_PATTERN_FILE_ENCODING = "utf-8";

	// config
	private String spanAnnoTypeName;
	private String resultAnnoTypeName;
	private URL patternFileUrl;
	private String patternFileEncoding;

	// derived
	private Type spanAnnoType;
	private Type resultAnnoType;
	private DictPatternsMatcher matcher;

	// state
	private Map<Long, Integer> patternMatchCounter = new HashMap<Long, Integer>();

	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		try {
			process(cas.getJCas());
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		spanAnnoType = ts.getType(spanAnnoTypeName);
		if (spanAnnoType == null) {
			throw new AnalysisEngineProcessException(
					new IllegalStateException("Type " + spanAnnoTypeName + " is not defined"));
		}
		resultAnnoType = ts.getType(resultAnnoTypeName);
		if (resultAnnoType == null) {
			throw new AnalysisEngineProcessException(
					new IllegalStateException("Type " + resultAnnoTypeName + " is not defined"));
		}
	}

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);

		spanAnnoTypeName = (String) ctx.getConfigParameterValue(PARAM_SPAN_ANNOTATION_TYPE);
		if (spanAnnoTypeName == null) {
			throw new ResourceInitializationException(
					new IllegalStateException("SpanAnnotationType param is NULL"));
		}
		resultAnnoTypeName = (String) ctx.getConfigParameterValue(PARAM_RESULT_ANNOTATION_TYPE);
		if (resultAnnoTypeName == null) {
			throw new ResourceInitializationException(
					new IllegalStateException("ResultAnnotationType param is NULL"));
		}
		try {
			patternFileUrl = getContext().getResourceURL(RESOURCE_PATTERN_FILE);
		} catch (ResourceAccessException e) {
			throw new ResourceInitializationException(e);
		}
		if (patternFileUrl == null) {
			throw new ResourceInitializationException(
					new IllegalStateException("PatternFile resource is NULL"));
		}
		patternFileEncoding = (String) ctx.getConfigParameterValue(PARAM_PATTERN_FILE_ENCODING);
		if (patternFileEncoding == null) {
			patternFileEncoding = DEFAULT_PATTERN_FILE_ENCODING;
		}
		try {
			matcher = new DictPatternsMatcher(patternFileUrl, patternFileEncoding);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();

		List<Map.Entry<Long, Integer>> patternCounters =
				Lists.newArrayList(patternMatchCounter.entrySet());
		Collections.sort(patternCounters, Collections.reverseOrder(
				new Comparator<Map.Entry<Long, Integer>>() {
					@Override
					public int compare(Entry<Long, Integer> a, Entry<Long, Integer> b) {
						return a.getValue().compareTo(b.getValue());
					}
				}));
		int total = 0;
		for (Map.Entry<Long, Integer> me : patternCounters) {
			total += me.getValue();
		}
		getContext().getLogger().log(Level.FINE, "Matches count: {0}\nTop five of patterns: {1}",
				new Object[] { total, patternCounters.subList(0, 5) });
		patternMatchCounter = new HashMap<Long, Integer>();
	}

	private void process(JCas cas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> spanIdx = cas.getAnnotationIndex(spanAnnoType);
		for (Annotation span : spanIdx) {
			processSpan(span);
		}
	}

	private void processSpan(Annotation span) {
		String spanTxt = span.getCoveredText();
		List<DictPatternMatch> matchList = matcher.match(spanTxt);
		for (DictPatternMatch match : matchList) {
			incrementPatternCounter(match.getPatternId());
			List<PatternElementSpan> matchElems = match.getMatchSpans();
			int beginOffset = matchElems.get(0).getBegin();
			int endOffset = matchElems.get(matchElems.size() - 1).getEnd();
			AnnotationFS resultAnno = span.getCAS()
					.createAnnotation(resultAnnoType, beginOffset, endOffset);
			span.getCAS().addFsToIndexes(resultAnno);
		}
	}

	private void incrementPatternCounter(long patternId) {
		Integer counter = patternMatchCounter.get(patternId);
		if (counter == null) {
			counter = 0;
		}
		counter++;
		patternMatchCounter.put(patternId, counter);
	}
}