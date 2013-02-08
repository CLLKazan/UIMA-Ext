/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno.impl;

import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.beans.factory.annotation.Autowired;

import ru.kfu.itis.cll.uima.eval.matching.Matcher;
import ru.kfu.itis.cll.uima.eval.matching.MatchingConfiguration;

/**
 * @author Rinat Gareev
 * 
 */
public class ConfigurableOverlapMatchingStrategy extends OverlapMatchingStrategy {

	@Autowired
	private MatchingConfiguration config;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean match(AnnotationFS goldAnno, AnnotationFS candAnno) {
		for (Matcher<AnnotationFS> m : config.getMatchers()) {
			if (!m.match(goldAnno, candAnno)) {
				return false;
			}
		}
		return true;
	}

}