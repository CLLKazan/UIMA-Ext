/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.resources;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.io.IoUtils.openReader;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.OTHER_PUNCTUATION_TAG;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.punctuationTagMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.morph.commons.TrainingDataWriterBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TokenInfoWriter extends TrainingDataWriterBase {

	static final String TYPE_WORD = "Word";
	static final String TYPE_NOT_WORD = "NotWord";

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
				writeInfo(TYPE_NOT_WORD, tokStr, tag);
			} else {
				Wordform wf = MorphCasUtils.requireOnlyWordform(word);
				String tag = tagMapper.toTag(FSUtils.toSet(wf.getGrammems()));
				writeInfo(TYPE_WORD, tokStr, tag);
			}
		}
		// write sentence end
		outputWriter.println();
	}

	private void writeInfo(String type, String token, String tag) {
		StringBuilder sb = new StringBuilder(token).append('\t').append(tag);
		sb.append('\t').append(type);
		outputWriter.println(sb);
	}

	private static Splitter infoLineSpitter = Splitter.on('\t');

	static TokenInfo parseLine(String line) {
		ArrayList<String> elems = Lists.newArrayList(infoLineSpitter.split(line));
		if (elems.size() != 3) {
			throw new IllegalArgumentException(String.format(
					"Can't parse the line:%n%s",
					line));
		}
		return new TokenInfo(elems.get(2), elems.get(0), elems.get(1));
	}

	static List<String> parseFileToTokens(File file) throws IOException {
		List<String> tokens = Lists.newLinkedList();
		BufferedReader trainSetReader = openReader(file);
		try {
			String line;
			while ((line = trainSetReader.readLine()) != null) {
				if (line.isEmpty()) {
					continue;
				}
				TokenInfo tokInfo = TokenInfoWriter.parseLine(line);
				tokens.add(tokInfo.token);
			}
		} finally {
			closeQuietly(trainSetReader);
		}
		return tokens;
	}
}

class TokenInfo {
	public final String type;
	public final String token;
	public final String tag;

	public TokenInfo(String type, String token, String tag) {
		this.type = type;
		this.token = token;
		this.tag = tag;
	}

	public boolean isWord() {
		return TokenInfoWriter.TYPE_WORD.equals(type);
	}
}