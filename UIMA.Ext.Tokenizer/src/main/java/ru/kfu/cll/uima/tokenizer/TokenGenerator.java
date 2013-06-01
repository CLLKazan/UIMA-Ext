package ru.kfu.cll.uima.tokenizer;

import org.apache.uima.jcas.JCas;
import ru.kfu.cll.uima.tokenizer.types.Letters;
import ru.kfu.cll.uima.tokenizer.types.Number;
import ru.kfu.cll.uima.tokenizer.types.Token;

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
    }
}
