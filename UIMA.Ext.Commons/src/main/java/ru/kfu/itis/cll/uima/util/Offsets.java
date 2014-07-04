/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import org.apache.uima.cas.text.AnnotationFS;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Offsets {
	private int begin;
	private int end;

	public Offsets(AnnotationFS anno) {
		this(anno.getBegin(), anno.getEnd());
	}

	public Offsets(int begin, int end) {
		this.begin = begin;
		this.end = end;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public boolean isIdenticalWith(AnnotationFS anno) {
		return anno.getBegin() == begin && anno.getEnd() == end;
	}
}
