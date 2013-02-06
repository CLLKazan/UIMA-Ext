/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import com.google.common.base.Objects;

/**
 * @author Rinat Gareev
 * 
 */
public class EqualityMatcher<T> implements Matcher<T> {

	@Override
	public boolean match(T ref, T cand) {
		return Objects.equal(ref, cand);
	}
}