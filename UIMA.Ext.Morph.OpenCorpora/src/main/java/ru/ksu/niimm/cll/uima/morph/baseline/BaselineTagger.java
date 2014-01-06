/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.addCasWordform;

import java.util.BitSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.AnnotationAdapter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.DefaultAnnotationAdapter;

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
	private WordformStore wfStore;
	@ConfigurationParameter(name = PARAM_USE_DEBUG_GRAMMEMS, defaultValue = "false")
	private boolean useDebugGrammems;
	private AnnotationAdapter wordAnnoAdapter;
	@ConfigurationParameter(name = PARAM_NUM_GRAMMEME)
	private String numGrammeme;
	// derived
	private BitSet numGramBS;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		wordAnnoAdapter = new DefaultAnnotationAdapter();
		wordAnnoAdapter.init(dict);
		//
		if (numGrammeme != null) {
			numGramBS = new BitSet();
			numGramBS.set(dict.getGrammemNumId(numGrammeme));
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Token token : JCasUtil.select(jCas, Token.class)) {
			if (!PUtils.canCarryWord(token)) {
				continue;
			}
			if (numGramBS != null && token instanceof NUM) {
				wordAnnoAdapter.apply(jCas, token, null, null, numGramBS);
				continue;
			}
			String tokenStr = token.getCoveredText();
			BitSet posBits = wfStore.getPosBits(tokenStr);
			if (posBits != null) {
				wordAnnoAdapter.apply(jCas, token, null, null, posBits);
			} else {
				if (useDebugGrammems) {
					setUnseen(jCas, addCasWordform(jCas, token));
				}
			}
		}
	}

	public static final String GRAMMEME_UNSEEN = "unseen";

	private void setUnseen(JCas jCas, org.opencorpora.cas.Wordform casWf) {
		casWf.setGrammems(FSUtils.toStringArray(jCas, GRAMMEME_UNSEEN));
	}
}