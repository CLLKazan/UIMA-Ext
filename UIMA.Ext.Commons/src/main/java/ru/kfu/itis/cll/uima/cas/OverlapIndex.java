/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import java.util.Set;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;

/**
 * Provides the contract to efficiently deal with search of overlapping
 * annotations. Use
 * {@link AnnotationUtils#createOverlapIndex(java.util.Iterator)} to get default
 * implementation.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface OverlapIndex<A extends AnnotationFS> {

	/**
	 * @param begin
	 * @param end
	 * @return set of annotations that overlap with annotation whose offsets are
	 *         given by parameters. Result ordering is defined by offsets
	 *         (according to {@link AnnotationIndex}. If offsets are equals then
	 *         source iterator ordering is used.
	 */
	Set<A> getOverlapping(int begin, int end);

}