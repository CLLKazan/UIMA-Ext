/**
 * 
 */
package ru.kfu.itis.issst.uima.ml;

import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.opencorpora.cas.Word;
import org.uimafit.util.ContainmentIndex;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.PM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.TokenBase;
import ru.kfu.cll.uima.tokenizer.fstype.WhiteSpace;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AdjacentPunctuationFeatureExtractor implements SimpleFeatureExtractor {

	public static final String FEATURE_NAME_BEFORE = "PMBefore";
	public static final String FEATURE_NAME_AFTER = "PMAfter";
	// config fields
	private final Map<String, String> markToFeatureValue;
	{
		Builder<String, String> mb = ImmutableMap.builder();
		mb.put(",", ",");
		mb.put(":", ":");
		mb.put(";", ";");
		mb.put("-", "-");
		// em dash
		mb.put("\u2014", "-");
		// en dash
		mb.put("\u2013", "-");
		mb.put(".", ".");
		mb.put("?", "?");
		mb.put("!", "!");
		mb.put("(", "(");
		mb.put(")", ")");
		markToFeatureValue = mb.build();
	}
	// state fields
	private ContainmentIndex<Sentence, Token> sentenceContIndex;
	private JCas view;

	public AdjacentPunctuationFeatureExtractor(JCas view) {
		this.view = view;
		sentenceContIndex = ContainmentIndex.create(view, Sentence.class, Token.class,
				ContainmentIndex.Type.REVERSE);
	}

	@Override
	public List<Feature> extract(JCas view, final Annotation focusAnnotation)
			throws CleartkExtractorException {
		if (this.view != view) {
			throw new IllegalStateException();
		}
		final Token focusToken;
		if (focusAnnotation instanceof Token) {
			focusToken = (Token) focusAnnotation;
		} else if (focusAnnotation instanceof Word) {
			focusToken = (Token) ((Word) focusAnnotation).getToken();
		} else {
			throw CleartkExtractorException.wrongAnnotationType(Word.class, focusAnnotation);
		}
		Sentence sent;
		{
			Collection<Sentence> sents = sentenceContIndex.containing(focusToken);
			if (sents.isEmpty()) {
				throw new IllegalStateException(String.format(
						"No sentence covers %s in %s", focusToken, getDocumentUri(view)));
			}
			if (sents.size() > 1) {
				throw new IllegalStateException(String.format(
						"Too much sentences cover %s in %s", focusToken, getDocumentUri(view)));
			}
			sent = sents.iterator().next();
		}
		// tb ~ TokenBase
		AnnotationIndex<Annotation> tbIndex = view.getAnnotationIndex(TokenBase.type);
		FSIterator<Annotation> tbIter = tbIndex.iterator(focusToken);
		if (!tbIter.isValid() && !tbIter.get().equals(focusToken)) {
			throw new IllegalStateException();
		}
		PM pmBefore;
		// skip whitespace before
		tbIter.moveToPrevious();
		while (tbIter.isValid() && (tbIter.get() instanceof WhiteSpace)) {
			tbIter.moveToPrevious();
		}
		if (!tbIter.isValid()) {
			// sentence begin
			pmBefore = null;
		} else if (tbIter.get() instanceof PM) {
			pmBefore = (PM) tbIter.get();
			if (pmBefore.getBegin() < sent.getBegin()) {
				// sentence begin
				pmBefore = null;
			}
		} else {
			pmBefore = null;
		}
		// return to focus
		tbIter.moveTo(focusToken);
		PM pmAfter;
		// skip whitespace after
		tbIter.moveToNext();
		while (tbIter.isValid() && (tbIter.get() instanceof WhiteSpace)) {
			tbIter.moveToNext();
		}
		if (!tbIter.isValid()) {
			// sentence end
			pmAfter = null;
		} else if (tbIter.get() instanceof PM) {
			pmAfter = (PM) tbIter.get();
			if (pmAfter.getEnd() > sent.getEnd()) {
				// sentence end
				pmAfter = null;
			}
		} else {
			pmAfter = null;
		}
		List<Feature> result = Lists.newLinkedList();
		Feature pmBeforeFeat = toFeature(pmBefore, FEATURE_NAME_BEFORE);
		if (pmBeforeFeat != null) {
			result.add(pmBeforeFeat);
		}
		Feature pmAfterFeat = toFeature(pmAfter, FEATURE_NAME_AFTER);
		if (pmAfterFeat != null) {
			result.add(pmAfterFeat);
		}
		return result;
	}

	private Feature toFeature(PM pmAnno, String featureName) {
		if (pmAnno == null) {
			return null;
		}
		String featValue = markToFeatureValue.get(pmAnno.getCoveredText());
		if (featValue == null) {
			return null;
		}
		return new Feature(featureName, featValue);
	}
}
