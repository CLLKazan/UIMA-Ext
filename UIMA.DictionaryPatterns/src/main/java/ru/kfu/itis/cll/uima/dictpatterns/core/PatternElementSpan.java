/**
 * 
 */
package ru.kfu.itis.cll.uima.dictpatterns.core;

/**
 * [begin, end)
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PatternElementSpan {

	private int begin;
	private int end;
	private String patternElement;

	public PatternElementSpan(int begin, int end, String patternElement) {
		this.begin = begin;
		this.end = end;
		if (end < begin) {
			throw new IllegalStateException();
		}
		this.patternElement = patternElement;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getPatternElement() {
		return patternElement;
	}
}