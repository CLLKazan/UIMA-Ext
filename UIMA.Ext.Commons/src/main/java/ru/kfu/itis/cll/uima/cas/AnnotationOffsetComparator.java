/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import java.util.Comparator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.CasUtil;

/**
 * @author Rinat Gareev
 * 
 */
public class AnnotationOffsetComparator<A extends AnnotationFS> implements Comparator<A> {

	public static <A extends AnnotationFS> AnnotationOffsetComparator<A> instance(Class<A> clazz) {
		return new AnnotationOffsetComparator<A>();
	}

	public static void checkForTheSameBoundaries(CAS cas, Class<? extends AnnotationFS> typeClass) {
		Type type = CasUtil.getType(cas, typeClass);
		FSIterator<AnnotationFS> iter = cas.getAnnotationIndex(type).iterator();
		iter.moveToFirst();
		if (!iter.isValid()) {
			return;
		}
		AnnotationOffsetComparator<AnnotationFS> cmp =
				AnnotationOffsetComparator.instance(AnnotationFS.class);
		AnnotationFS lastAnno = iter.get();
		iter.moveToNext();
		while (iter.isValid()) {
			AnnotationFS anno = iter.get();
			if (cmp.compare(anno, lastAnno) == 0) {
				throw new IllegalStateException(String.format(
						"Annotations %s and %s have the same boundaries",
						lastAnno, anno));
			}
			iter.moveToNext();
		}
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