/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.ConstraintFactory;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.FSTypeConstraint;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FSUtils {

	public static JCas getJCas(FeatureStructure fs) {
		try {
			return fs.getCAS().getJCas();
		} catch (CASException e) {
			throw new RuntimeException(e);
		}
	}

	public static FSArray toFSArray(JCas cas, Collection<? extends FeatureStructure> srcCol) {
		FSArray result = new FSArray(cas, srcCol.size());
		int i = 0;
		for (FeatureStructure fs : srcCol) {
			result.set(i, fs);
			i++;
		}
		return result;
	}

	public static StringArray toStringArray(JCas cas, Collection<String> srcCol) {
		StringArray result = new StringArray(cas, srcCol.size());
		int i = 0;
		for (String gr : srcCol) {
			result.set(i, gr);
			i++;
		}
		return result;
	}

	public static Set<String> toSet(StringArrayFS fsArr) {
		if (fsArr == null)
			return ImmutableSet.of();
		ImmutableSet.Builder<String> resultBuilder = ImmutableSet.builder();
		for (int i = 0; i < fsArr.size(); i++) {
			resultBuilder.add(fsArr.get(i));
		}
		return resultBuilder.build();
	}

	public static FSTypeConstraint getTypeConstraint(Type firstType, Type... otherTypes) {
		FSTypeConstraint constr = ConstraintFactory.instance().createTypeConstraint();
		constr.add(firstType);
		for (Type t : otherTypes) {
			constr.add(t);
		}
		return constr;
	}

	public static FSTypeConstraint getTypeConstraint(String firstType, String... otherTypes) {
		FSTypeConstraint constr = ConstraintFactory.instance().createTypeConstraint();
		constr.add(firstType);
		for (String t : otherTypes) {
			constr.add(t);
		}
		return constr;
	}

	public static <FST extends FeatureStructure> List<FST> filterToList(CAS cas,
			FSIterator<FST> srcIter, FSMatchConstraint... constraints) {
		FSIterator<FST> resultIter = filter(cas, srcIter, constraints);
		return toList(resultIter);
	}

	public static <F extends FeatureStructure> FSIterator<F> filter(CAS cas,
			FSIterator<F> srcIter, FSMatchConstraint... constraints) {
		if (constraints.length == 0) {
			return srcIter;
		}
		FSMatchConstraint resultConstr = and(constraints);
		return cas.createFilteredIterator(srcIter, resultConstr);
	}

	public static <F extends FeatureStructure> List<F> filter(List<F> srcList,
			FSMatchConstraint... constraints) {
		if (constraints.length == 0) {
			return ImmutableList.copyOf(srcList);
		}
		ArrayList<F> resultList = Lists.newArrayListWithCapacity(srcList.size());
		FSMatchConstraint conj = and(constraints);
		for (F fs : srcList) {
			if (conj.match(fs)) {
				resultList.add(fs);
			}
		}
		return Collections.unmodifiableList(resultList);
	}

	public static FSMatchConstraint and(FSMatchConstraint... constraints) {
		if (constraints.length == 0) {
			throw new IllegalArgumentException("Constraints array are empty");
		}
		ConstraintFactory cf = ConstraintFactory.instance();
		FSMatchConstraint resultConstr = constraints[0];
		for (int i = 1; i < constraints.length; i++) {
			resultConstr = cf.and(resultConstr, constraints[i]);
		}
		return resultConstr;
	}

	public static <FST extends FeatureStructure> List<FST> toList(FSIterator<FST> iter) {
		LinkedList<FST> result = newLinkedList();
		fill(iter, result);
		return result;
	}

	public static <FST extends FeatureStructure> Set<FST> toSet(FSIterator<FST> iter) {
		HashSet<FST> result = newHashSet();
		fill(iter, result);
		return result;
	}

	public static <FST extends FeatureStructure> void fill(FSIterator<FST> srcIter,
			Collection<FST> destCol) {
		srcIter.moveToFirst();
		while (srcIter.isValid()) {
			destCol.add(srcIter.get());
			srcIter.moveToNext();
		}
	}

	/*
	 * Note that getIntValue will return 0 if feature value is not set.
	 */
	public static int intMinBy(Iterable<? extends FeatureStructure> fsCollection, Feature intFeat) {
		Integer min = Integer.MAX_VALUE;
		boolean hasResult = false;
		for (FeatureStructure fs : fsCollection) {
			int intValue = fs.getIntValue(intFeat);
			hasResult = true;
			if (intValue < min) {
				min = intValue;
			}
		}
		if (!hasResult) {
			throw new IllegalArgumentException("fsCollection is empty");
		}
		return min;
	}

	/*
	 * Note that getIntValue will return 0 if feature value is not set.
	 */
	public static int intMaxBy(Iterable<? extends FeatureStructure> fsCollection, Feature intFeat) {
		Integer max = Integer.MIN_VALUE;
		boolean hasResult = false;
		for (FeatureStructure fs : fsCollection) {
			int intValue = fs.getIntValue(intFeat);
			hasResult = true;
			if (intValue > max) {
				max = intValue;
			}
		}
		if (!hasResult) {
			throw new IllegalArgumentException("fsCollection is empty");
		}
		return max;
	}

	private FSUtils() {
	}

}