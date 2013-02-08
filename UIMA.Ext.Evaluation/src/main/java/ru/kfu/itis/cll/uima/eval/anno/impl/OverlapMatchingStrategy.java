/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno.impl;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.getOverlapping;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toSet;

import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.beans.factory.annotation.Autowired;

import ru.kfu.itis.cll.uima.eval.anno.AnnotationExtractor;
import ru.kfu.itis.cll.uima.eval.anno.MatchingStrategy;

/**
 * @author Rinat Gareev
 * 
 */
public abstract class OverlapMatchingStrategy implements MatchingStrategy {

	@Autowired
	private AnnotationExtractor annotationExtractor;

	@Override
	public Set<AnnotationFS> searchCandidates(AnnotationFS goldAnno, CAS sysCas) {
		Set<AnnotationFS> result = toSet(getOverlapping(
				sysCas, annotationExtractor.extract(sysCas), goldAnno));
		return result;
	}

	@Override
	public AnnotationFS searchExactMatch(AnnotationFS goldAnno, Iterable<AnnotationFS> candidates) {
		for (AnnotationFS candAnno : candidates) {
			if (match(goldAnno, candAnno)) {
				return candAnno;
			}
		}
		return null;
	}

	protected abstract boolean match(AnnotationFS goldAnno, AnnotationFS candAnno);
}