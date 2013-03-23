/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

/**
 * TODO matchers graph can be cyclic => avoid infinite recursion in 'toString'
 * method
 * 
 * @author Rinat Gareev
 * 
 */
public interface Matcher<T> {

	boolean match(T ref, T cand);

	void print(StringBuilder out, T value);

}