package ru.kfu.cll.uima.tokenizer;

import ru.kfu.cll.uima.tokenizer.types.Token;
import ru.kfu.cll.uima.tokenizer.types.Letters;
import ru.kfu.cll.uima.tokenizer.types.Number;
import ru.kfu.cll.uima.tokenizer.types.Separator;
import ru.kfu.cll.uima.tokenizer.types.PM;
import ru.kfu.cll.uima.tokenizer.types.Bracket;
import ru.kfu.cll.uima.tokenizer.types.Symbol;
import ru.kfu.cll.uima.tokenizer.types.Range;
import ru.kfu.cll.uima.tokenizer.types.Abbrevation;
import ru.kfu.cll.uima.tokenizer.types.Currensy;
import ru.kfu.cll.uima.tokenizer.types.Measurement;
import ru.kfu.cll.uima.tokenizer.types.ComplexWord;
import ru.kfu.cll.uima.tokenizer.types.Date;
import ru.kfu.cll.uima.tokenizer.types.Email;
import ru.kfu.cll.uima.tokenizer.types.Abbrevation;
import ru.kfu.cll.uima.tokenizer.types.RussianWord;
import org.apache.uima.jcas.JCas;

/**
 * Created with IntelliJ IDEA.
 * User: marsel
 * Date: 22.05.13
 * Time: 1:21
 * To change this template use File | Settings | File Templates.
 */
public class TokenGenerator {
    private JCas UIMA_JCas;
    public TokenGenerator(JCas UIMA_JCas) {
        this.UIMA_JCas = UIMA_JCas;
    }

    public Token Generate(String typeOfToken, int begin, int end, String args[]) {
        Token token = new Token(UIMA_JCas);
        token.setNorm(null);
        token.setText(args[0]);
        token.setBegin(begin);
        token.setEnd(end);
        if (typeOfToken.equals("Letters")) {
            Letters letters = (Letters) token;
            letters.setLanguage(args[1]);
            letters.setLetterCase(args[2]);
            return letters;
        } else if (typeOfToken.equals("Number")) {
            Number number = (Number) token;
            number.setKind(args[1]);
            number.setSign(args[2]);
            return number;
        }
        else {
            return null;
        }
    }
}
