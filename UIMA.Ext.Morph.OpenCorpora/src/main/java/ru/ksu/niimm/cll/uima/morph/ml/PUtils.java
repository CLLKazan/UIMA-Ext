/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.opencorpora.cas.Word;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.Token;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class PUtils {

	public static Word getWordAnno(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {
		Word wordAnno = null;
		if (focusAnnotation instanceof Word) {
			wordAnno = (Word) focusAnnotation;
		} else if (focusAnnotation instanceof Token) {
			List<Word> wordsCovered = JCasUtil.selectCovered(Word.class, focusAnnotation);
			if (!wordsCovered.isEmpty()) {
				wordAnno = wordsCovered.get(0);
			}
		} else {
			throw CleartkExtractorException.wrongAnnotationType(Word.class, focusAnnotation);
		}
		return wordAnno;
	}

	private PUtils() {
	}
}
