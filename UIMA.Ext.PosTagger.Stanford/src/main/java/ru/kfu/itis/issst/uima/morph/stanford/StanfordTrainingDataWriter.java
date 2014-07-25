/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.stanford;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.OTHER_PUNCTUATION_TAG;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.punctuationTagMap;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.TagUtils;
import ru.kfu.itis.issst.uima.morph.commons.TrainingDataWriterBase;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class StanfordTrainingDataWriter extends TrainingDataWriterBase {

	public static final String CLOSED_CLASS_TAGS_FILENAME = "closed-class-tags.txt";

	// per-collection state-fields
	private SortedSet<String> closedClassTags = Sets.newTreeSet();

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		closedClassTags.addAll(punctuationTagMap.values());
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		File closedClassTagsFile = new File(outputDir, CLOSED_CLASS_TAGS_FILENAME);
		try {
			FileUtils.writeLines(closedClassTagsFile, "utf-8", closedClassTags);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		closedClassTags = Sets.newTreeSet();
	}

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
				Wordform wf = MorphCasUtils.requireOnlyWordform(word);
				String tag = wf.getPos();
				writeTokenTag(tokStr, tag);
				if (tag != null && TagUtils.isClosedClassTag(tag)) {
					closedClassTags.add(tag);
				}
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
