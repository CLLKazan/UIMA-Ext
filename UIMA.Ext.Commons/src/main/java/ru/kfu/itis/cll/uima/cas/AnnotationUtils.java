/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import static com.google.common.collect.Lists.newArrayList;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.cas.ConstraintFactory;
import org.apache.uima.cas.FSIntConstraint;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AnnotationUtils {

	public static int length(Annotation anno) {
		return anno.getEnd() - anno.getBegin();
	}

	/**
	 * 
	 * @param first
	 * @param second
	 * @return does given annotation overlap or not. See
	 *         {@link #getOverlapping(JCas, FSIterator, Annotation)} for
	 *         overlapping definition.
	 */
	public static boolean overlap(Annotation first, Annotation second) {
		return (first.getBegin() < second.getBegin() && first.getEnd() > second.getBegin())
				|| (first.getBegin() >= second.getBegin() && first.getBegin() < second.getEnd());
	}

	/**
	 * 
	 * @param first
	 * @param second
	 * @return overlap length or 0 if given annotation do not overlap. See
	 *         {@link #getOverlapping(JCas, FSIterator, Annotation)} for
	 *         overlapping (overlapping) definition.
	 */
	public static int overlapLength(Annotation first, Annotation second) {
		if (first.getBegin() < second.getBegin() && first.getEnd() > second.getBegin()) {
			return Math.min(first.getEnd(), second.getEnd()) - second.getBegin();
		} else if (first.getBegin() >= second.getBegin() && first.getBegin() < second.getEnd()) {
			return Math.min(first.getEnd(), second.getEnd()) - first.getBegin();
		}
		return 0;
	}

	/**
	 * Given:
	 * <p>
	 * target.begin = tb, target.end = te,
	 * </p>
	 * <p>
	 * requested.begin = rb, requested.end = re,
	 * </p>
	 * <p>
	 * overlapping constraint is: (tb&lt;rb && te&gt;rb) || (tb&ge;rb &&
	 * tb&lt;re)
	 * </p>
	 * 
	 * @param cas
	 * @param iter
	 *            source iterator
	 * @param targetAnno
	 *            annotation which result annotations must overlap with
	 * @return iterator over annotations overlapping with targetAnno from source
	 *         iterator
	 */
	public static FSIterator<Annotation> getOverlapping(JCas cas, FSIterator<Annotation> iter,
			Annotation targetAnno) {
		ConstraintFactory cf = ConstraintFactory.instance();
		FSMatchConstraint firstDisjunct;
		{
			FSIntConstraint beginConstraint = cf.createIntConstraint();
			beginConstraint.lt(targetAnno.getBegin());
			FSIntConstraint endConstraint = cf.createIntConstraint();
			endConstraint.gt(targetAnno.getBegin());
			firstDisjunct = cf.and(
					cf.embedConstraint(newArrayList("begin"), beginConstraint),
					cf.embedConstraint(newArrayList("end"), endConstraint));
		}
		FSMatchConstraint secondDisjunct;
		{
			FSIntConstraint beginConstraint = cf.createIntConstraint();
			beginConstraint.geq(targetAnno.getBegin());
			beginConstraint.lt(targetAnno.getEnd());
			secondDisjunct = cf.embedConstraint(newArrayList("begin"), beginConstraint);
		}
		FSMatchConstraint overlapConstraint = cf.or(firstDisjunct, secondDisjunct);
		return cas.createFilteredIterator(iter, overlapConstraint);
	}

	public static <FST extends FeatureStructure> List<FST> toList(FSIterator<FST> iter) {
		LinkedList<FST> result = new LinkedList<FST>();
		iter.moveToFirst();
		while (iter.isValid()) {
			result.add(iter.get());
			iter.moveToNext();
		}
		return result;
	}

	private AnnotationUtils() {
	}

}