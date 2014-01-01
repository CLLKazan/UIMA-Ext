/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.*;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.TrainingDataWriterBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class HunposTrainingDataWriter extends TrainingDataWriterBase {

	@Override
	protected void processSentence(JCas jCas, List<Token> tokens)
			throws AnalysisEngineProcessException {
		for (Token tok : tokens) {
			Word word = getWordOfToken(tok);
			String tokStr = tok.getCoveredText();
			if (word == null) {
				if (tok instanceof NUM || tok instanceof W) {
					getLogger().warn(String.format(
							"Token %s in %s does not have corresponding Word annotation",
							toPrettyString(tok), getDocumentUri(jCas)));
					continue;
				}
				String tag = punctuationTagMap.get(tokStr);
				if (tag == null) {
					tag = OTHER_PUNCTUATION_TAG;
				}
				writeTokenTag(tokStr, tag);
			} else {
				FSArray wfs = word.getWordforms();
				if (wfs == null || wfs.size() == 0) {
					throw new IllegalStateException(String.format(
							"No wordforms in Word %s in %s",
							toPrettyString(word), getDocumentUri(jCas)));
				}
				Wordform wf = (Wordform) wfs.get(0);
				String tag = tagMapper.toTag(wf);
				writeTokenTag(tokStr, tag);
			}
		}
		// write sentence end
		outputWriter.println();
	}

	private void writeTokenTag(String token, String tag) {
		StringBuilder sb = new StringBuilder(token).append('\t').append(tag);
		outputWriter.println(sb);
	}
}