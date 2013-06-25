/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class RusCorporaAnnotation {
	private int begin = -1;
	private int end = -1;

	public RusCorporaAnnotation(int begin) {
		this.begin = begin;
	}

	public RusCorporaAnnotation(int begin, int end) {
		this(begin);
		this.end = end;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}
}