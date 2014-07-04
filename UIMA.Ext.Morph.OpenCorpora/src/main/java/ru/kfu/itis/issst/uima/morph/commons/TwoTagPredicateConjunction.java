/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TwoTagPredicateConjunction implements TwoTagPredicate {

	public static TwoTagPredicateConjunction and(TwoTagPredicate... operands) {
		return new TwoTagPredicateConjunction(Arrays.asList(operands));
	}

	private final List<TwoTagPredicate> operands;

	public TwoTagPredicateConjunction(List<TwoTagPredicate> operands) {
		this.operands = ImmutableList.copyOf(operands);
	}

	@Override
	public boolean apply(BitSet first, BitSet second) {
		for (TwoTagPredicate inner : operands) {
			if (!inner.apply(first, second)) {
				return false;
			}
		}
		return true;
	}

}
