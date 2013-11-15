/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.addCasWordform;
import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.normalizeToDictionary;

import java.util.BitSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.AnnotationAdapter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.DefaultAnnotationAdapter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SuffixExaminingPosTagger extends SuffixExaminingPosAnnotator {

	public static final String RESOURCE_WFSTORE = "WfStore";
	public static final String PARAM_IGNORE_EXISTING_WORDS = "ignoreExistingWords";
	public static final String PARAM_USE_DEBUG_GRAMMEMS = "useDebugGrammems";
	// config fields
	@ConfigurationParameter(name = PARAM_IGNORE_EXISTING_WORDS, defaultValue = "true")
	private boolean ignoreExistingWords;
	@ExternalResource(key = RESOURCE_WFSTORE, mandatory = true)
	private WordformStore wfStore;
	private AnnotationAdapter annoAdapter;
	@ConfigurationParameter(name = PARAM_USE_DEBUG_GRAMMEMS, defaultValue = "false")
	private boolean useDebugGrammems;
	// state fields
	private int tokensTagged;
	private int tokensIgnored;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		annoAdapter = new DefaultAnnotationAdapter();
		annoAdapter.init(dict);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Token token : JCasUtil.select(jCas, Token.class)) {
			if (!PUtils.canCarryWord(token)) {
				continue;
			}
			if (ignoreExistingWords && JCasUtil.contains(jCas, token, Word.class)) {
				tokensIgnored++;
				continue;
			}
			String tokenStr = normalizeToDictionary(token.getCoveredText());
			if (tokenStr.length() > suffixLength) {
				String suffix = getSuffix(tokenStr);
				String suffixKey = makeSuffixKey(suffix);
				BitSet posBits = wfStore.getPosBits(suffixKey);
				if (posBits != null) {
					annoAdapter.apply(jCas, token, null, null, posBits);
				} else {
					// TODO (*) make this configurable
					Wordform wf = addCasWordform(jCas, token);
					if (useDebugGrammems) {
						setUnknownSuffix(jCas, wf);
					}
				}
			} else {
				BitSet posBits = wfStore.getPosBits(tokenStr);
				if (posBits != null) {
					annoAdapter.apply(jCas, token, null, null, posBits);
				} else {
					// TODO (*) make this configurable
					Wordform wf = addCasWordform(jCas, token);
					if (useDebugGrammems) {
						setUnknownShortWord(jCas, wf);
					}
				}
			}
			tokensTagged++;
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		getLogger().info(String.format("Tokens tagged: %s\nTokens ignored: %s",
				tokensTagged, tokensIgnored));
	}

	// debug grammemes
	public static final String GRAMMEME_UNKNOWN_SUFFIX = "unknown-suffix";
	public static final String GRAMMEME_UNKNOWN_SHORT_WORD = "unknown-short-word";

	private void setUnknownSuffix(JCas jCas, Wordform wordformFS) {
		wordformFS.setGrammems(FSUtils.toStringArray(jCas, GRAMMEME_UNKNOWN_SUFFIX));
	}

	private void setUnknownShortWord(JCas jCas, Wordform wordformFS) {
		wordformFS.setGrammems(FSUtils.toStringArray(jCas, GRAMMEME_UNKNOWN_SHORT_WORD));
	}
}