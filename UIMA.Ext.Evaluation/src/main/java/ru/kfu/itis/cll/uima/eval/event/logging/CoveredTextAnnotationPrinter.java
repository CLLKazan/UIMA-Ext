/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event.logging;

import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CoveredTextAnnotationPrinter implements AnnotationPrinter {

	private static final CoveredTextAnnotationPrinter INSTANCE = new CoveredTextAnnotationPrinter();

	public static CoveredTextAnnotationPrinter getInstance() {
		return INSTANCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getString(AnnotationFS anno) {
		return anno == null ? null : anno.getCoveredText();
	}

	@Override
	public void init(TypeSystem ts) {
	}
}