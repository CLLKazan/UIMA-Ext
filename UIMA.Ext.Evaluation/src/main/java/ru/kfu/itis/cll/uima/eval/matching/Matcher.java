/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

/**
 * 
 * @author Rinat Gareev
 * 
 */
public interface Matcher<T> {

	boolean match(T ref, T cand);

	void print(StringBuilder out, T value);
}