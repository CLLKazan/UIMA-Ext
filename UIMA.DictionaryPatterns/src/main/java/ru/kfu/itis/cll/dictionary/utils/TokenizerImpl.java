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
public class TokenizerImpl implements Tokenizer {

	/*
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private TokenizerImpl instance = new TokenizerImpl();

		public Builder includeDelimeters(boolean includeDelimeters) {
			instance.includeDelimeters = includeDelimeters;
			return this;
		}

		public Builder removeFromSeparators(char ch) {
			instance.separators = StringUtils.replace(
					instance.separators,
					String.valueOf(ch), "");
			return this;
		}

		public TokenizerImpl build() {
			return instance;
		}
	}*/

	private boolean includeDelimeters;

	public TokenizerImpl() {
		this(false);
	}

	public TokenizerImpl(boolean includeDelimeters) {
		this.includeDelimeters = includeDelimeters;
	}

	@Override
	public List<Token> tokenize(String str) {
		if (str == null) {
			return null;
		}
		if (str.isEmpty()) {
			return Collections.emptyList();
		}
		List<Token> result = Lists.newArrayList();
		State state = State.START;
		int lastStateStart = 0;
		int i = 0;
		while (i < str.length()) {
			char curCh = str.charAt(i);
			if (!state.contain(curCh)) {
				if (i - lastStateStart > 0) {
					postprocess(state, result, str, lastStateStart, i);
				}
				// change state
				lastStateStart = i;
				state = State.getStateFor(curCh);
			}
			i++;
		}
		postprocess(state, result, str, lastStateStart, str.length());
		return result;
	}

	private void postprocess(State state, List<Token> resultList, String input, int from, int to) {
		switch (state) {
		case DELIM:
			postprocessDelimeter(resultList, input, from, to);
			break;
		case SPACE:
			break;
		case TOKEN: {
			postprocessToken(resultList, input, from, to);
			break;
		}
		default:
			throw new UnsupportedOperationException("Unknow State enum");
		}
	}

	private void postprocessDelimeter(List<Token> resultList, String input, int from, int to) {
		if (!includeDelimeters) {
			return;
		}
		String delimStr = input.substring(from, to);
		delimStr = delimStr.trim();
		if (!delimStr.isEmpty()) {
			resultList.add(new Token(from, to, delimStr));
		}
	}

	protected void postprocessToken(List<Token> resultList, String input, int from, int to) {
		resultList.add(new Token(from, to, input.substring(from, to)));
	}

	/**
	 * 
	 * @param str
	 * @return true if str starts with separator char
	 */
	public boolean isDelimeter(Token token) {
		return State.DELIM.contain(token.getString().charAt(0));
	}

	/*
	 * ORDERING OF INSTANCES INSIDE THIS ENUM IS CRUCIAL!
	 */
	private enum State {
		SPACE {
			@Override
			public boolean contain(char ch) {
				return Character.isWhitespace(ch);
			}
		},
		TOKEN {
			@Override
			public boolean contain(char ch) {
				return Character.isLetterOrDigit(ch);
			}
		},
		DELIM {
			@Override
			public boolean contain(char ch) {
				return !TOKEN.contain(ch) && !SPACE.contain(ch);
			}
		},
		START {
			@Override
			public boolean contain(char ch) {
				return false;
			}
		};

		public abstract boolean contain(char ch);

		public static State getStateFor(char ch) {
			for (State state : values()) {
				if (state.contain(ch)) {
					return state;
				}
			}
			throw new IllegalStateException("Unknown state for character '" + ch + "'");
		}
	}

	public static void main(String[] args) {
		String str = "-dasf1 ,,- fdsgsg2 aaa333- - ";
		System.out.println(str);
		System.out.println(new TokenizerImpl().tokenize(str));
		System.out.println(
				new TokenizerImpl().tokenize(str).size());
		System.out.println(new TokenizerImpl(true).tokenize(str));
		System.out.println(
				new TokenizerImpl(true).tokenize(str).size());
	}
}