/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.util.List;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CaseNormalizingTokenizer implements Tokenizer {
	private Tokenizer orig;

	public CaseNormalizingTokenizer(Tokenizer orig) {
		this.orig = orig;
	}

	public List<Token> tokenize(String str) {
		List<Token> list = orig.tokenize(str);
		for (Token token : list) {
			token.setString(token.getString().toUpperCase());
		}
		return list;
	}

}