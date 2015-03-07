/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;

import com.google.common.collect.Sets;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SpecialWTokenRemover extends JCasAnnotator_ImplBase {
	
	public static AnalysisEngineDescription createDescription()
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createEngineDescription(SpecialWTokenRemover.class);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Map<Token, Word> token2WordIndex = MorphCasUtils.getToken2WordIndex(jCas);
		Set<Token> tokens2Remove = Sets.newHashSet();
		for (Token token : JCasUtil.select(jCas, Token.class)) {
			Word word = token2WordIndex.get(token);
			if (word == null && (token instanceof NUM || token instanceof W)) {
				getLogger().warn(String.format(
						"Token %s in %s does not have corresponding Word annotation",
						toPrettyString(token), getDocumentUri(jCas)));
				tokens2Remove.add(token);
			}
		}
		for (Token token : tokens2Remove) {
			token.removeFromIndexes();
		}
	}
}
