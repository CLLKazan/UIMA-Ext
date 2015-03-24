package ru.kfu.itis.issst.uima.test;

import org.apache.uima.jcas.JCas;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.apache.uima.fit.factory.AnnotationFactory;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.PM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;

/**
 * @author Rinat Gareev
 */
public class TestCasBuilder {
    //cfg
    private JCas jCas;
    //state
    private StringBuilder buf = new StringBuilder();
    private Token sentenceFirstToken = null;

    public TestCasBuilder(JCas jCas) {
        this.jCas = jCas;
    }

    public TestCasBuilder word(String txt, String... grams) {
        space();
        Token w = w(jCas, txt);
        Word word = new Word(jCas, w.getBegin(), w.getEnd());
        word.setToken(w);
        Wordform wf = new Wordform(jCas);
        wf.setGrammems(FSUtils.toStringArray(jCas, grams));
        word.setWordforms(FSUtils.toFSArray(jCas, wf));
        word.addToIndexes();
        return this;
    }

    public TestCasBuilder punct(String txt) {
        space();
        pm(jCas, txt);
        return this;
    }

    public TestCasBuilder sentenceEnd(String punctTxt) {
        punct(punctTxt);
        int begin = sentenceFirstToken.getBegin();
        int end = buf.length();
        new Sentence(jCas, begin, end).addToIndexes();
        sentenceFirstToken = null;
        return this;
    }

    public TestCasBuilder space() {
        buf.append(' ');
        return this;
    }

    public void apply() {
        jCas.setDocumentText(buf.toString());
    }

    private Token pm(JCas jCas, String txt) {
        int begin = buf.length();
        buf.append(txt);
        int end = buf.length();
        return token(jCas, begin, end, PM.class);
    }

    private Token w(JCas jCas, String txt) {
        int begin = buf.length();
        buf.append(txt);
        int end = buf.length();
        return token(jCas, begin, end, W.class);
    }

    private Token token(JCas jCas, int begin, int end, Class<? extends Token> tokClass) {
        Token result;
        result = AnnotationFactory.createAnnotation(jCas, begin, end, tokClass);
        if (sentenceFirstToken == null)
            sentenceFirstToken = result;
        return result;
    }
}
