/**
 * 
 */
package ru.kfu.itis.cll.uima.dictpatterns.core;

/**
 * [from, to)
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
public class PatternElementSpan {
	
	private int from;
	private int to;
	private String patternElement;
	
	public PatternElementSpan(int from, int to, String patternElement) {
		this.from = from;
		this.to = to;
		this.patternElement = patternElement;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public String getPatternElement() {
		return patternElement;
	}
}