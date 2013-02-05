/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.util.Comparator;

import org.apache.uima.cas.text.AnnotationFS;

import com.google.common.collect.ComparisonChain;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class AnnotationOffsetComparator implements Comparator<AnnotationFS> {

	public static final AnnotationOffsetComparator INSTANCE = new AnnotationOffsetComparator();

	@Override
	public int compare(AnnotationFS first, AnnotationFS second) {
		if (first == second) {
			return 0;
		}
		return ComparisonChain.start()
				.compare(first.getBegin(), second.getBegin())
				.compare(second.getEnd(), first.getEnd())
				.compare(first.getType().getName(), second.getType().getName())
				.result();
	}
}