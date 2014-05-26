/**
 * 
 */
package ru.kfu.itis.issst.uima.segmentation;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.component.CasAnnotator_ImplBase;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.CW;
import ru.kfu.cll.uima.tokenizer.fstype.EXCLAMATION;
import ru.kfu.cll.uima.tokenizer.fstype.PERIOD;
import ru.kfu.cll.uima.tokenizer.fstype.QUESTION;
import ru.kfu.cll.uima.tokenizer.fstype.SW;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import com.google.common.collect.ImmutableSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SentenceSplitter extends CasAnnotator_ImplBase {

	public static AnalysisEngineDescription createDescription()
			throws ResourceInitializationException {
		TypeSystemDescription tsDesc = createTypeSystemDescription(
				"ru.kfu.itis.issst.uima.segmentation.segmentation-TypeSystem");
		return createPrimitiveDescription(SentenceSplitter.class, tsDesc);
	}

	private final String[] sentenceEndTokenTypeNames = new String[] {
			PERIOD.class.getName(), EXCLAMATION.class.getName(),
			QUESTION.class.getName()
	};
	// derived
	private Set<Type> sentenceEndTokenTypes;

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);

		sentenceEndTokenTypes = new HashSet<Type>();
		for (String curTypeName : sentenceEndTokenTypeNames) {
			Type curType = ts.getType(curTypeName);
			annotationTypeExist(curTypeName, curType);
			sentenceEndTokenTypes.add(curType);
		}
		sentenceEndTokenTypes = ImmutableSet.copyOf(sentenceEndTokenTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		try {
			process(aCAS.getJCas());
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private void process(JCas cas) throws AnalysisEngineProcessException {
		// consider only non-whitespace tokens
		AnnotationIndex<Annotation> tokenIdx = cas.getAnnotationIndex(Token.type);
		if (tokenIdx.size() == 0) {
			return;
		}
		String txt = cas.getDocumentText();
		FSIterator<Annotation> tokensIter = tokenIdx.iterator();
		// get first sentence start
		tokensIter.moveToFirst();
		Token lastSentenceStart = (Token) tokensIter.get();
		tokensIter.moveToNext();

		while (tokensIter.isValid()) {
			Token token = (Token) tokensIter.get();
			Token nextToken = (Token) lookupNext(tokensIter);
			if (sentenceEndTokenTypes.contains(token.getType()) &&
					(nextToken == null
							||
							isBreakBetween(txt, token, nextToken)
							||
					(distanceBetween(token, nextToken) > 0
							&& !isAbbreviationBefore(tokensIter)
							&& !isSWAfter(tokensIter)))) {
				// sanity check conditions evaluation
				if (token != tokensIter.get()) {
					throw new IllegalStateException(String.format(
							"Failed on token %s", token));
				}
				makeSentence(cas, lastSentenceStart, token);
				tokensIter.moveToNext();
				if (tokensIter.isValid()) {
					lastSentenceStart = (Token) tokensIter.get();
				} else {
					lastSentenceStart = null;
				}
			} else {
				tokensIter.moveToNext();
			}
		}
		if (lastSentenceStart != null) {
			// here tokensIter is INVALID so we should call moveToLast
			tokensIter.moveToLast();
			Token token = (Token) tokensIter.get();
			makeSentence(cas, lastSentenceStart, token);
			lastSentenceStart = null;
		}
	}

	private boolean isAbbreviationBefore(FSIterator<Annotation> tokensIter) {
		Annotation tokenBefore = lookupPrevious(tokensIter);
		if (tokenBefore == null) {
			return false;
		}
		return tokenBefore.getTypeIndexID() == CW.type &&
				tokenBefore.getEnd() - tokenBefore.getBegin() == 1;
	}

	private boolean isSWAfter(FSIterator<Annotation> tokensIter) {
		Annotation tokenAfter = lookupNext(tokensIter);
		if (tokenAfter == null) {
			return false;
		}
		return tokenAfter.getTypeIndexID() == SW.type;
	}

	private void makeSentence(JCas cas, Token firstToken, Token lastToken) {
		int begin = firstToken.getBegin();
		int end = lastToken.getEnd();
		if (end <= begin) {
			throw new IllegalStateException(String.format(
					"Illegal start and end token for sentence: %s, %s",
					firstToken, lastToken));
		}
		Sentence sentence = new Sentence(cas, begin, end);
		sentence.setFirstToken(firstToken);
		sentence.setLastToken(lastToken);
		sentence.addToIndexes();
	}

	/**
	 * Return next element if exists. Always save iterator position.
	 * 
	 * @param iter
	 *            iterator
	 * @return next element if exists or null otherwise
	 */
	private static <T extends FeatureStructure> T lookupNext(FSIterator<T> iter) {
		iter.moveToNext();
		T result;
		if (iter.isValid()) {
			result = iter.get();
			iter.moveToPrevious();
		} else {
			result = null;
			iter.moveToLast();
		}
		return result;
	}

	/**
	 * Return previous element if exists. Always save iterator position.
	 * 
	 * @param iter
	 *            iterator
	 * @return previous element if exists or null otherwise
	 */
	private static <T extends FeatureStructure> T lookupPrevious(FSIterator<T> iter) {
		iter.moveToPrevious();
		T result;
		if (iter.isValid()) {
			result = iter.get();
			iter.moveToNext();
		} else {
			result = null;
			iter.moveToFirst();
		}
		return result;
	}

	/**
	 * @param anno1
	 * @param anno2
	 * @return 0 if given annotation overlap else return distance between the
	 *         end of first (in text direction) annotation and the begin of
	 *         second annotation.
	 */
	private static int distanceBetween(AnnotationFS anno1, AnnotationFS anno2) {
		AnnotationFS first;
		AnnotationFS second;
		if (anno1.getBegin() > anno2.getBegin()) {
			first = anno2;
			second = anno1;
		} else if (anno1.getBegin() < anno2.getBegin()) {
			first = anno1;
			second = anno2;
		} else {
			return 0;
		}
		int result = second.getBegin() - first.getEnd();
		return result >= 0 ? result : 0;
	}

	private static boolean isBreakBetween(String txt, Annotation first, Annotation second) {
		for (int i = first.getEnd(); i < second.getBegin(); i++) {
			if (txt.charAt(i) == '\n') {
				return true;
			}
		}
		return false;
	}
}