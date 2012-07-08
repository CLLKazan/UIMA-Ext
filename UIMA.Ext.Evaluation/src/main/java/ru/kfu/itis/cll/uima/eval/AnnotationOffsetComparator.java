/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.util.Comparator;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class AnnotationOffsetComparator implements Comparator<Annotation> {

	public static final AnnotationOffsetComparator INSTANCE = new AnnotationOffsetComparator();

	@Override
	public int compare(Annotation first, Annotation second) {
		return Integer.valueOf(first.getBegin()).compareTo(second.getBegin());
	}
}