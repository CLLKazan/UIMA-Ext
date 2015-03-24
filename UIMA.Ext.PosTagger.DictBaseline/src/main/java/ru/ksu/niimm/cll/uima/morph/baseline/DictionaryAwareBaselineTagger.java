/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.kfu.itis.issst.uima.postagger.MorphCasUtils.addCasWordform;

import java.util.BitSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.wfstore.WordformStore;
import ru.kfu.itis.issst.uima.morph.dictionary.AnnotationAdapter;
import ru.kfu.itis.issst.uima.morph.dictionary.WordUtils;
import ru.kfu.itis.issst.uima.postagger.DefaultAnnotationAdapter;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.postagger.PosTrimmer;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryAwareBaselineTagger extends DictionaryAwareBaselineAnnotator {

	public static final String PARAM_USE_DEBUG_GRAMMEMS = "useDebugGrammems";
	public static final String PARAM_NUM_GRAMMEME = "numGrammeme";
	public static final String RESOURCE_WFSTORE = "WordformStore";

	// config fields
	@ExternalResource(key = RESOURCE_WFSTORE, mandatory = true)
	private WordformStore<BitSet> wfStore;
	@ConfigurationParameter(name = PARAM_USE_DEBUG_GRAMMEMS, defaultValue = "false", mandatory = false)
	private boolean useDebugGrammems;
	private AnnotationAdapter wordAnnoAdapter;
	@ConfigurationParameter(name = PARAM_NUM_GRAMMEME, mandatory = false)
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
			numGramBS.set(gramModel.getGrammemNumId(numGrammeme));
		}
		// init pos-trimmer
		String[] targetPosCategories = wfStore.getProperty(PARAM_TARGET_POS_CATEGORIES,
				String[].class);
		posTrimmer = new PosTrimmer(gramModel, targetPosCategories);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Token token : JCasUtil.select(jCas, Token.class)) {
			if (!PosTaggerAPI.canCarryWord(token)) {
				continue;
			}
			if (numGramBS != null && token instanceof NUM) {
				wordAnnoAdapter.apply(jCas, token, null, null, numGramBS);
				continue;
			}
			String tokenStr = token.getCoveredText();
			tokenStr = WordUtils.normalizeToDictionaryForm(tokenStr);
			Set<BitSet> dictEntries = trimAndMergePosBits(dict.getEntries(tokenStr));
			if (dictEntries == null || dictEntries.isEmpty()) {
				if (useDebugGrammems) {
					setNotDictionary(jCas, addCasWordform(jCas, token));
				}
			} else if (dictEntries.size() == 1) {
				wordAnnoAdapter.apply(jCas, token, null, null, dictEntries.iterator().next());
			} else {
				BitSet posBits = wfStore.getTag(tokenStr);
				if (posBits != null) {
					wordAnnoAdapter.apply(jCas, token, null, null, posBits);
				} else {
					if (useDebugGrammems) {
						setAmbiguous(jCas, addCasWordform(jCas, token));
					}
				}
			}
		}
	}

	public static final String GRAMMEME_NOT_DICT = "not-dict";
	public static final String GRAMMEME_AMBIGUOUS = "ambiguous";

	private void setNotDictionary(JCas jCas, org.opencorpora.cas.Wordform casWf) {
		casWf.setGrammems(FSUtils.toStringArray(jCas, GRAMMEME_NOT_DICT));
	}

	private void setAmbiguous(JCas jCas, org.opencorpora.cas.Wordform casWf) {
		casWf.setGrammems(FSUtils.toStringArray(jCas, GRAMMEME_AMBIGUOUS));
	}
}