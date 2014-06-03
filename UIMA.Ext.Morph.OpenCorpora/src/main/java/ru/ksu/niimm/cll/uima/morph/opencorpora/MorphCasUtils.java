/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.AnnotationUtils;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.util.DocumentUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphCasUtils {

	private static final Logger log = LoggerFactory.getLogger(MorphCasUtils.class);

	public static void addGrammeme(JCas jCas, Wordform wf, String newGram) {
		addGrammemes(jCas, wf, ImmutableList.of(newGram));
	}

	public static void addGrammemes(JCas jCas, Wordform wf, Iterable<String> newGrams) {
		LinkedHashSet<String> wfGrams = Sets.newLinkedHashSet(FSUtils.toSet(wf.getGrammems()));
		boolean changed = false;
		for (String newGram : newGrams) {
			changed |= wfGrams.add(newGram);
		}
		if (changed) {
			wf.setGrammems(FSUtils.toStringArray(jCas, wfGrams));
		}
	}

	/**
	 * @param word
	 * @return the first wordform in the given Word annotation, or null if there
	 *         is no any wordform.
	 */
	public static Wordform getOnlyWordform(Word word) {
		FSArray wfs = word.getWordforms();
		if (wfs == null || wfs.size() == 0) {
			return null;
		}
		if (wfs.size() > 1) {
			log.warn("Too much wordforms for Word {} in {}",
					AnnotationUtils.toPrettyString(word),
					DocumentUtils.getDocumentUri(word.getCAS()));
		}
		return (Wordform) wfs.get(0);
	}

	/**
	 * @param word
	 * @return the first wordform in the given Word annotation, never null
	 */
	public static Wordform requireOnlyWordform(Word word) {
		Wordform wf = getOnlyWordform(word);
		if (wf == null) {
			throw new IllegalStateException(String.format(
					"No wordforms in Word %s in %s",
					toPrettyString(word), getDocumentUri(word.getCAS())));
		}
		return wf;
	}

	public static Map<Token, Word> getToken2WordIndex(JCas jCas) {
		Map<Token, Word> result = Maps.newHashMap();
		for (Word word : JCasUtil.select(jCas, Word.class)) {
			Token token = (Token) word.getToken();
			if (token == null) {
				throw new IllegalStateException(String.format(
						"No token assigned for Word %s in %s",
						toPrettyString(word), getDocumentUri(jCas)));
			}
			if (result.put(token, word) != null) {
				throw new IllegalStateException(String.format(
						"Shared token for Word %s in %s",
						toPrettyString(word), getDocumentUri(jCas)));
			}
		}
		return result;
	}

	public static Map<Token, Word> getToken2WordIndex(JCas jCas, AnnotationFS span) {
		Map<Token, Word> result = Maps.newHashMap();
		for (Word word : JCasUtil.selectCovered(jCas, Word.class, span)) {
			Token token = (Token) word.getToken();
			if (token == null) {
				throw new IllegalStateException(String.format(
						"No token assigned for Word %s in %s",
						toPrettyString(word), getDocumentUri(jCas)));
			}
			if (result.put(token, word) != null) {
				throw new IllegalStateException(String.format(
						"Shared token for Word %s in %s",
						toPrettyString(word), getDocumentUri(jCas)));
			}
		}
		return result;
	}

	private MorphCasUtils() {
	}

}