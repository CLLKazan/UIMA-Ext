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

	private static final EqualityMatcher<?> INSTANCE = new EqualityMatcher<Object>();

	@SuppressWarnings("unchecked")
	public static final <T> EqualityMatcher<T> getInstance() {
		return (EqualityMatcher<T>) INSTANCE;
	}

	private EqualityMatcher() {
	}

	@Override
	public boolean match(T ref, T cand) {
		return Objects.equal(ref, cand);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public void print(StringBuilder out, T value) {
		out.append(value);
	}
}