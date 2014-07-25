/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;
import ru.kfu.itis.issst.uima.morph.commons.TrainingDataWriterBase;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

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
				String tag = PunctuationUtils.getPunctuationTag(tokStr);
				writeTokenTag(tokStr, tag);
			} else {
				Wordform wf = MorphCasUtils.requireOnlyWordform(word);
				String tag = wf.getPos();
				writeTokenTag(tokStr, tag);
			}
		}
		// write sentence end
		outputWriter.println();
	}

	private void writeTokenTag(String token, String tag) {
		StringBuilder sb = new StringBuilder(token).append('\t').append(tag);
		// \r\n (CRLF) does not work for Windows build of Hunpos
		// so we have to append LF explicitly
		// TODO actually this did not help either
		sb.append('\n');
		outputWriter.print(sb);
	}
}
