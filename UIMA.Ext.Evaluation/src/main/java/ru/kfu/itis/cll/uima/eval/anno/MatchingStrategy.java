/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno;

import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;

/**
 * @author Rinat Gareev
 * 
 */
public interface MatchingStrategy {

	void changeCas(CAS newSysCas);

	Set<AnnotationFS> searchCandidates(AnnotationFS goldAnno);

	/**
	 * 
	 * @param godlAnno
	 * @param candidates
	 * @return return the first candidate that exactly matches the given
	 *         goldAnno
	 */
	AnnotationFS searchExactMatch(AnnotationFS goldAnno, Iterable<AnnotationFS> candidates);

}