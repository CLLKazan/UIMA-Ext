package ru.kfu.cll.uima.tokenizer;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.CasUtil;
import static ru.kfu.itis.cll.uima.cas.FSUtils.*;

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

	private TokenUtils() {
	}
}