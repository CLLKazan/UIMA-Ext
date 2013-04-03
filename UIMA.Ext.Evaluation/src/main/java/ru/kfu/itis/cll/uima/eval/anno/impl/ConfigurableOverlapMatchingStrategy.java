/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno.impl;

import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;

import ru.kfu.itis.cll.uima.eval.matching.TypeBasedMatcherDispatcher;

/**
 * @author Rinat Gareev
 * 
 */
public class ConfigurableOverlapMatchingStrategy extends OverlapMatchingStrategy {

	@Autowired
	private TypeBasedMatcherDispatcher<AnnotationFS> topMatcher;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean match(AnnotationFS goldAnno, AnnotationFS candAnno) {
		return topMatcher.match(goldAnno, candAnno);
	}

	@Override
	protected boolean isCandidate(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		return Objects.equal(goldAnno.getType(), sysAnno.getType());
	}
}