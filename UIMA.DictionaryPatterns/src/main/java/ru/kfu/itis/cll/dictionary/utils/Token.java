/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Token represents string span [begin, end) I.e. 'end' property points to the
 * first character after end of the token
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Token {

	private int begin;
	private int end;
	private String string;

	public Token(int begin, int end, String string) {
		this.begin = begin;
		this.end = end;
		this.string = string;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}

	public String getString() {
		return string;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("string", string).append("begin", begin).append("end", end).toString();
	}
}