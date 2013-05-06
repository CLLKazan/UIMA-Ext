/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;

/**
 * @author Rinat Gareev
 * 
 */
public interface AnnotationExtractor {

	// TODO could we return some other non-UIMA class? Like java.util.Iterator
	FSIterator<AnnotationFS> extract(CAS cas);

}