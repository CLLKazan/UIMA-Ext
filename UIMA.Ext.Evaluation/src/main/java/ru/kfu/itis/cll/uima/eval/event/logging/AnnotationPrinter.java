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
public interface AnnotationPrinter {

	void init(TypeSystem ts);

	String getString(AnnotationFS anno);

}