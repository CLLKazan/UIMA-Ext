/**
 * 
 */
package ru.kfu.itis.issst.uima.segmentation;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import ru.kfu.cll.uima.segmentation.fstype.QSegment;
import ru.kfu.cll.uima.tokenizer.fstype.PM;

import com.google.common.collect.Lists;

/**
 * TODO handle "«" and "»" (and other) quotation marks! Use SegmentationUtils
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class QuotationAnnotator extends CasAnnotator_ImplBase {

	@ConfigurationParameter(name = "spanAnnotationType",
			defaultValue = "ru.kfu.cll.uima.segmentation.fstype.Sentence")
	private String spanAnnotationTypeName;
	// derived
	private Type spanAnnotationType;
	private Logger log;

	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		try {
			process(cas.getJCas());
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private void process(JCas cas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> pmIndex = cas.getAnnotationIndex(PM.type);
		AnnotationIndex<Annotation> spanIdx = cas.getAnnotationIndex(spanAnnotationType);
		for (Annotation span : spanIdx) {
			process(cas, pmIndex, span);
		}
	}

	private void process(JCas cas, AnnotationIndex<Annotation> pmIndex, Annotation span) {
		// TODO what about type priorities?
		FSIterator<Annotation> pmIter = pmIndex.subiterator(span);
		// qm = 'quotation mark'
		List<Annotation> qmList = Lists.newLinkedList();
		while (pmIter.hasNext()) {
			Annotation pm = pmIter.next();
			// TODO handle other quotation marks! See TODO in the class header
			if ("\"".equals(pm.getCoveredText())) {
				qmList.add(pm);
			}
		}
		if (qmList.isEmpty()) {
			return;
		}
		if (qmList.size() == 1) {
			info("Following span contains independent quotation mark:\n%s",
					span.getCoveredText());
			return;
		}
		if (qmList.size() == 2) {
			createQSegment(cas, qmList.get(0), qmList.get(1), null);
		} else if (qmList.size() == 3) {
			QSegment parentSeg = createQSegment(cas, qmList.get(0), qmList.get(2), null);
			createQSegment(cas, qmList.get(1), qmList.get(2), parentSeg);
		} else {
			// more than 3 quotation marks
			// TODO
			/*
			LinkedList<Annotation> qmStack = Lists.newLinkedList();
			// first is always opening one
			Iterator<Annotation> qmIter = qmList.iterator();
			qmStack.add(qmIter.next());
			while (qmIter.hasNext()) {
				Annotation qm = qmIter.next();
				boolean wsAfter = isWhitespaceAfter(cas, qm);
				boolean wsBefore = isWhitespaceBefore(cas, qm);
				if (wsAfter == wsBefore) {
					info("Can't define qSegments in:\n%s", span.getCoveredText());
					break;
				}
				if(!wsBefore){
					// close segment
					Annotation openingQM = qmStack.pollLast();
					if(openingQM)
					createQSegment(cas, openingQM, qm, null);
				}
			}
			*/
		}
	}

	private void info(String msg, Object... args) {
		log.log(Level.INFO, String.format(msg, args));
	}

	private boolean isWhitespaceAfter(JCas cas, Annotation anno) {
		char charAfter = cas.getDocumentText().charAt(anno.getEnd());
		return Character.isWhitespace(charAfter);
	}

	private boolean isWhitespaceBefore(JCas cas, Annotation anno) {
		if (anno.getBegin() == 0) {
			return true;
		}
		char charBefore = cas.getDocumentText().charAt(anno.getBegin() - 1);
		return Character.isWhitespace(charBefore);
	}

	private QSegment createQSegment(JCas cas, Annotation openingQM, Annotation closingQM,
			Annotation parentSegment) {
		return createQSegment(cas, openingQM, closingQM, parentSegment, true);
	}

	private QSegment createQSegment(JCas cas, Annotation openingQM, Annotation closingQM,
			Annotation parentSegment, boolean addToIndex) {
		// sanity check
		if (closingQM.getBegin() - openingQM.getEnd() < 0) {
			throw new IllegalStateException();
		}
		QSegment seg = new QSegment(cas);
		seg.setBegin(openingQM.getBegin());
		seg.setEnd(closingQM.getEnd());
		seg.setContentBegin(openingQM.getEnd());
		seg.setContentEnd(closingQM.getBegin());
		if (parentSegment != null) {
			seg.setParentSegment(parentSegment);
		}
		if (addToIndex) {
			seg.addToIndexes();
		}
		return seg;
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		spanAnnotationType = ts.getType(spanAnnotationTypeName);
		annotationTypeExist(spanAnnotationTypeName, spanAnnotationType);
	}

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		log = context.getLogger();
	}
}