/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.OTHER_PUNCTUATION_TAG;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.punctuationTagMap;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.TrainingDataWriterBase;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TTTrainingDataWriter extends TrainingDataWriterBase {

	public static final String LEXICON_FILENAME = "training-data.lex";
	// default TT sentence end tag 
	public static final String TAG_SENT = "SENT";
	private static final String SYNTHETIC_SENTENCE_END_TOKEN = ".";
	// state fields
	private Multimap<String, String> outputLexicon;
	// statistics
	private int syntheticSentEnds;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		outputLexicon = TreeMultimap.create();
	}

	@Override
	protected void processSentence(JCas jCas, List<Token> tokens)
			throws AnalysisEngineProcessException {
		final Token lastTok = tokens.get(tokens.size() - 1);
		boolean hasSentenceEnd = false;
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
				String tag;
				if (tok == lastTok) {
					// sentence end
					tag = TAG_SENT;
					hasSentenceEnd = true;
				} else {
					tag = punctuationTagMap.get(tokStr);
					if (tag == null) {
						tag = OTHER_PUNCTUATION_TAG;
					}
				}
				writeTT(tokStr, tag);
			} else {
				Wordform wf = MorphCasUtils.requireOnlyWordform(word);
				String tag = wf.getPos();
				if (!isDigitalNumber(tokStr)) {
					// null means NONLEX
					if (tag != null) {
						outputLexicon.put(normalizeForLexicon(tokStr), tag);
					}
				}
				writeTT(tokStr, tag);
			}
		}
		if (!hasSentenceEnd) {
			writeTT(SYNTHETIC_SENTENCE_END_TOKEN, TAG_SENT);
			syntheticSentEnds++;
		}
	}

	private void writeTT(String token, String tag) {
		StringBuilder sb = new StringBuilder(token).append('\t').append(tag);
		outputWriter.println(sb);
	}

	private static String normalizeForLexicon(String str) {
		return str.toLowerCase();
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		getLogger().info("Synthetic sentence-end tokens were added: " + syntheticSentEnds);
		File lexiconFile = new File(outputDir, LEXICON_FILENAME);
		try {
			LexiconWriter.write(outputLexicon, lexiconFile);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		outputLexicon = null;
		super.collectionProcessComplete();
	}

	@Override
	public void destroy() {
		outputLexicon = null;
		super.destroy();
	}

	private static final Pattern digitalNumberPattern = Pattern.compile("\\d+");

	private boolean isDigitalNumber(String tok) {
		return digitalNumberPattern.matcher(tok).matches();
	}
}