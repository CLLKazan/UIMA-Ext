/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Tokenizer {

	private String separators = " -=+,./\\:;!?@#$%^&*()[]{}`~<>";

	private boolean includeDelimeters;

	public Tokenizer() {
		this(false);
	}

	public Tokenizer(boolean includeDelimeters) {
		this.includeDelimeters = includeDelimeters;
	}

	public List<Token> tokenize(String str) {
		if (str == null) {
			return null;
		}
		if (str.isEmpty()) {
			return Collections.emptyList();
		}
		List<Token> result = Lists.newArrayList();
		State state = null;
		int lastStateStart = 0;
		int i = 0;
		while (i < str.length()) {
			if (separators.indexOf(str.charAt(i)) > -1) {
				if (state != State.DELIM) {
					if (i - lastStateStart > 0) {
						postprocessToken(result, str, lastStateStart, i);
					}
					lastStateStart = i;
					state = State.DELIM;
				}
			} else {
				if (state != State.TOKEN) {
					if (i - lastStateStart > 0 && includeDelimeters) {
						postprocessDelimeter(result, str, lastStateStart, i);
					}
					lastStateStart = i;
					state = State.TOKEN;
				}
			}
			i++;
		}
		if (state == State.DELIM && includeDelimeters) {
			postprocessDelimeter(result, str, lastStateStart, str.length());
		} else if (state == State.TOKEN) {
			postprocessToken(result, str, lastStateStart, str.length());
		}
		return result;
	}

	private void postprocessDelimeter(List<Token> resultList, String input, int from, int to) {
		String delimStr = input.substring(from, to);
		delimStr = delimStr.trim();
		if (!delimStr.isEmpty()) {
			resultList.add(new Token(from, to, delimStr));
		}
	}

	private void postprocessToken(List<Token> resultList, String input, int from, int to) {
		resultList.add(new Token(from, to, input.substring(from, to)));
	}

	/**
	 * 
	 * @param str
	 * @return true if str starts with separator char
	 */
	public boolean isDelimeter(Token token) {
		return separators.indexOf(token.getString().charAt(0)) > -1;
	}

	private enum State {
		DELIM, TOKEN
	}

	public static void main(String[] args) {
		System.out.println(new Tokenizer().tokenize("-dasf1 ,,- fdsgsg2 aaa333- - "));
		System.out.println(
				new Tokenizer().tokenize("-dasf1 ,,- fdsgsg2 aaa333- - ").size());
		System.out.println(new Tokenizer(true).tokenize("-dasf1 ,,- fdsgsg2 aaa333- - "));
		System.out.println(
				new Tokenizer(true).tokenize("-dasf1 ,,- fdsgsg2 aaa333- - ").size());
	}
}