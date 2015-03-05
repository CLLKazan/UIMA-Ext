/**
 * 
 */
package ru.kfu.itis.issst.uima.ml;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;

/**
 * Auxiliary annotator that makes Word annotations for each token that can carry
 * them.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class WordAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		makeWords(jCas);
	}

	public static void makeWords(JCas jCas) {
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			for (Token tok : JCasUtil.selectCovered(jCas, Token.class, sent)) {
				if (canCarryWord(tok)) {
					Word word = new Word(jCas);
					word.setBegin(tok.getBegin());
					word.setEnd(tok.getEnd());
					word.setToken(tok);

					Wordform wf = new Wordform(jCas);
					wf.setWord(word);
					word.setWordforms(FSUtils.toFSArray(jCas, wf));
					word.addToIndexes();
				}
			}
		}
	}

    public static boolean canCarryWord(Token tok) {
        return tok instanceof W || tok instanceof NUM;
    }
}
