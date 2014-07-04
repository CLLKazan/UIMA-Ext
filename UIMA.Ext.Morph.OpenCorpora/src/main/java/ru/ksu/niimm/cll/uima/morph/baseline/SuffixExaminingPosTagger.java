/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.addCasWordform;
import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.normalizeToDictionary;

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
import ru.kfu.itis.issst.uima.morph.commons.TagUtils;

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
	private WordformStore<String> wfStore;
	@ConfigurationParameter(name = PARAM_USE_DEBUG_GRAMMEMS, defaultValue = "false")
	private boolean useDebugGrammems;
	// state fields
	private int tokensTagged;
	private int tokensIgnored;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		if (suffixLength > 0) {
			throw new IllegalStateException("PARAM_SUFFIX_LENGTH must not be specified. " +
					"It is restored from a model.");
		}
		suffixLength = wfStore.getProperty(KEY_SUFFIX_LENGTH, Integer.class);
		if (suffixLength == null) {
			throw new IllegalStateException(
					"Can't restore the suffix length parameter from the model");
		}
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
				Wordform wf = addCasWordform(jCas, token);
				String suffix = getSuffix(tokenStr);
				String suffixKey = makeSuffixKey(suffix);
				String tag = wfStore.getTag(suffixKey);
				if (tag != null) {
					tag = TagUtils.postProcessExternalTag(tag);
					wf.setPos(tag);
				} else {
					if (useDebugGrammems) {
						setUnknownSuffix(jCas, wf);
					}
				}
			} else {
				Wordform wf = addCasWordform(jCas, token);
				String tag = wfStore.getTag(tokenStr);
				if (tag != null) {
					tag = TagUtils.postProcessExternalTag(tag);
					wf.setPos(tag);
				} else {
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
		wordformFS.setPos(GRAMMEME_UNKNOWN_SUFFIX);
	}

	private void setUnknownShortWord(JCas jCas, Wordform wordformFS) {
		wordformFS.setPos(GRAMMEME_UNKNOWN_SHORT_WORD);
	}
}