package ru.kfu.cll.uima.tokenizer;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.CasUtil;

import static ru.kfu.itis.cll.uima.cas.FSUtils.*;
import ru.kfu.cll.uima.tokenizer.fstype.CAP;
import ru.kfu.cll.uima.tokenizer.fstype.CW;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.SPECIAL;
import ru.kfu.cll.uima.tokenizer.fstype.SW;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

public class TokenUtils {

	public static Token getTokenBefore(Token refToken) {
		return getTokenRelative(refToken, -1);
	}

	public static Token getTokenAfter(Token refToken) {
		return getTokenRelative(refToken, 1);
	}

	public static Token getTokenRelative(Token refToken, int index) {
		CAS cas = refToken.getCAS();
		Type tokenType = cas.getTypeSystem().getType(Token.class.getName());
		try {
			return (Token) CasUtil.selectSingleRelative(cas, tokenType, refToken, index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	// TODO test
	public static boolean areAdjoining(Token t1, Token t2) {
		JCas jCas = getJCas(t1);
		FSIterator<Annotation> tokenIter = jCas.getAnnotationIndex(Token.typeIndexID).iterator();
		tokenIter.moveTo(t1);
		assert (t1.equals(tokenIter.get()));
		tokenIter.moveToNext();
		return tokenIter.isValid() && tokenIter.get().equals(t2);
	}

	/**
	 * Use this method on texts that were tokenized externally.
	 * 
	 * @param jCas
	 * @param str
	 * @param begin
	 * @param end
	 * @return Token annotation of appropriate subtype (W,CW,NUM,etc.)
	 */
	// TODO use InitialTokenizer & PostTokenizer to implement this method
	public static Token makeToken(JCas jCas, String str, int begin, int end) {
		if (str.isEmpty()) {
			throw new IllegalStateException(String.format(
					"Empty token (%s,%s)", begin, end));
		}
		// search for letter
		int firstLetterIdx = -1;
		for (int i = 0; i < str.length(); i++) {
			if (Character.isLetter(str.charAt(i))) {
				firstLetterIdx = i;
				break;
			}
		}
		if (firstLetterIdx < 0) {
			// no letters, search for digit
			boolean hasDigit = false;
			for (int i = 0; i < str.length() && !hasDigit; i++) {
				if (Character.isDigit(str.charAt(i))) {
					hasDigit = true;
				}
			}
			if (hasDigit) {
				return new NUM(jCas, begin, end);
			} else {
				return new SPECIAL(jCas, begin, end);
			}
		}
		char firstLetter = str.charAt(firstLetterIdx);
		if (Character.isUpperCase(firstLetter)) {
			// check for CAP
			boolean allCap = true;
			for (int i = 0; i < str.length() && allCap; i++) {
				char ch = str.charAt(i);
				if (Character.isLetter(ch) && !Character.isUpperCase(ch)) {
					allCap = false;
				}
			}
			return allCap ? new CAP(jCas, begin, end) : new CW(jCas, begin, end);
		} else {
			return new SW(jCas, begin, end);
		}
	}

	private TokenUtils() {
	}
}