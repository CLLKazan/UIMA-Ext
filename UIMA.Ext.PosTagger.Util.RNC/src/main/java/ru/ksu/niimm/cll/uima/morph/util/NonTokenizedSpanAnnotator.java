/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.util;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;

import ru.kfu.cll.uima.tokenizer.fstype.TokenBase;
import ru.ksu.niimm.cll.uima.morph.util.NonTokenizedSpan;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class NonTokenizedSpanAnnotator extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		String txt = jCas.getDocumentText();
		if (txt == null || txt.isEmpty()) {
			return;
		}
		int txtLength = txt.length();
		AnnotationIndex<Annotation> tokenIndex = jCas
				.getAnnotationIndex(TokenBase.typeIndexID);
		FSIterator<Annotation> tokenIter = tokenIndex.iterator();
		if (!tokenIter.isValid()) {
			makeNTSpan(jCas, 0, txtLength);
		} else {
			int lastTokenEnd = 0;
			while (tokenIter.isValid()) {
				Annotation token = tokenIter.get();
				if (token.getBegin() > lastTokenEnd) {
					makeNTSpan(jCas, lastTokenEnd, token.getBegin());
				}
				lastTokenEnd = token.getEnd();
				//
				tokenIter.moveToNext();
			}
			// check the span after the last token
			if (txtLength > lastTokenEnd) {
				makeNTSpan(jCas, lastTokenEnd, txtLength);
			}
		}
	}

	private void makeNTSpan(JCas jCas, int begin, int end) {
		assert end > begin;
		new NonTokenizedSpan(jCas, begin, end).addToIndexes();
	}
}