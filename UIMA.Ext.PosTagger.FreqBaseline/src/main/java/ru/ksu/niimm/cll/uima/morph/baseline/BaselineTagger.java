/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils.addCasWordform;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.morph.commons.TagUtils;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BaselineTagger extends BaselineAnnotator {

	public static final String PARAM_USE_DEBUG_GRAMMEMS = "useDebugGrammems";
	public static final String PARAM_NUM_GRAMMEME = "numGrammeme";
	public static final String RESOURCE_WFSTORE = "WordformStore";

	// config fields
	@ExternalResource(key = RESOURCE_WFSTORE, mandatory = true)
	private WordformStore<String> wfStore;
	@ConfigurationParameter(name = PARAM_USE_DEBUG_GRAMMEMS, defaultValue = "false")
	private boolean useDebugGrammems;
	@ConfigurationParameter(name = PARAM_NUM_GRAMMEME)
	private String numGrammeme;

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Token token : JCasUtil.select(jCas, Token.class)) {
			if (!PosTaggerAPI.canCarryWord(token)) {
				continue;
			}
			if (numGrammeme != null && token instanceof NUM) {
				addCasWordform(jCas, token).setPos(numGrammeme);
				continue;
			}
			String tokenStr = token.getCoveredText();
			String tag = wfStore.getTag(tokenStr);
			if (tag != null) {
				tag = TagUtils.postProcessExternalTag(tag);
				addCasWordform(jCas, token).setPos(tag);
			} else {
				if (useDebugGrammems) {
					setUnseen(jCas, addCasWordform(jCas, token));
				}
			}
		}
	}

	public static final String GRAMMEME_UNSEEN = "unseen";

	private void setUnseen(JCas jCas, org.opencorpora.cas.Wordform casWf) {
		casWf.setPos(GRAMMEME_UNSEEN);
	}
}