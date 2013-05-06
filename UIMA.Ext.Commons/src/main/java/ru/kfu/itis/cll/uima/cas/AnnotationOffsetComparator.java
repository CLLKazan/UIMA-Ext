/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import java.util.Comparator;

import org.apache.uima.cas.text.AnnotationFS;

/**
 * @author Rinat Gareev
 * 
 */
public class AnnotationOffsetComparator<A extends AnnotationFS> implements Comparator<A> {

	public static <A extends AnnotationFS> AnnotationOffsetComparator<A> instance(Class<A> clazz) {
		return new AnnotationOffsetComparator<A>();
	}

	@Override
	public int compare(A a1, A a2) {
		if (a1.getBegin() < a2.getBegin()) {
			return -1;
		} else if (a1.getBegin() > a2.getBegin()) {
			return 1;
		} else if (a1.getEnd() < a2.getEnd()) {
			return 1;
		} else if (a1.getEnd() > a2.getEnd()) {
			return -1;
		} else {
			return 0;
		}
	}

}