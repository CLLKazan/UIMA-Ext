/**
 * 
 */
package ru.kfu.cll.uima.segmentation;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

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

	@ConfigurationParameter(name = "sentenceType")
	private String sentenceTypeName = "ru.kfu.cll.uima.segmentation.fstype.Sentence";
	private final String[] sentenceEndTokenTypeNames = new String[] {
			PERIOD.class.getName(), EXCLAMATION.class.getName(),
			QUESTION.class.getName()
	};
	// derived
	private Type sentenceType;
	private Set<Type> sentenceEndTokenTypes;

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		sentenceType = ts.getType(sentenceTypeName);
		annotationTypeExist(sentenceTypeName, sentenceType);

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
		FSIterator<Annotation> tokensIter = tokenIdx.iterator();
		// get first sentence start
		tokensIter.moveToFirst();
		Annotation lastSentenceStart = tokensIter.get();
		tokensIter.moveToNext();

		while (tokensIter.isValid()) {
			Annotation token = tokensIter.get();
			if (sentenceEndTokenTypes.contains(token.getType())
					&& !isAbbreviationBefore(tokensIter)
					&& !isSWAfter(tokensIter)) {
				// sanity check conditions evaluation
				if (token != tokensIter.get()) {
					throw new IllegalStateException(String.format(
							"Failed on token %s", token));
				}
				makeSentence(cas, lastSentenceStart, token);
				tokensIter.moveToNext();
				if (tokensIter.isValid()) {
					lastSentenceStart = tokensIter.get();
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
			Annotation token = tokensIter.get();
			makeSentence(cas, lastSentenceStart, token);
			lastSentenceStart = null;
		}
	}

	private boolean isAbbreviationBefore(FSIterator<Annotation> tokensIter) {
		tokensIter.moveToPrevious();
		if (!tokensIter.isValid()) {
			tokensIter.moveToFirst();
			return false;
		}
		try {
			Annotation tokenBefore = tokensIter.get();
			if (tokenBefore.getTypeIndexID() == CW.type
					&& tokenBefore.getEnd() - tokenBefore.getBegin() == 1) {
				return true;
			}
			return false;
		} finally {
			tokensIter.moveToNext();
		}
	}

	private boolean isSWAfter(FSIterator<Annotation> tokensIter) {
		tokensIter.moveToNext();
		if (!tokensIter.isValid()) {
			tokensIter.moveToLast();
			return false;
		}
		try {
			Annotation tokenAfter = tokensIter.get();
			if (tokenAfter.getTypeIndexID() == SW.type) {
				return true;
			}
			return false;
		} finally {
			tokensIter.moveToPrevious();
		}
	}

	private void makeSentence(JCas cas, Annotation firstToken, Annotation lastToken) {
		int begin = firstToken.getBegin();
		int end = lastToken.getEnd();
		if (end <= begin) {
			throw new IllegalStateException(String.format(
					"Illegal start and end token for sentence: %s, %s",
					firstToken, lastToken));
		}
		AnnotationFS sentence = cas.getCas().createAnnotation(sentenceType, begin, end);
		cas.addFsToIndexes(sentence);
	}
}