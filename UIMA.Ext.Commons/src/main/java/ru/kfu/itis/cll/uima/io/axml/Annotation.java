/**
 * 
 */
package ru.kfu.itis.cll.uima.io.axml;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class Annotation {

	private String type;
	private int begin;
	private int end;

	Annotation() {
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public void setEnd(int end) {
		this.end = end;
	}

}
