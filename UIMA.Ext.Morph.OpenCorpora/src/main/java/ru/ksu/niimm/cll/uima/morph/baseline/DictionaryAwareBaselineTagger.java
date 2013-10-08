/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform.allGramBitsFunction;

import java.util.BitSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.AnnotationAdapter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.DefaultAnnotationAdapter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

import com.google.common.collect.Iterables;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryAwareBaselineTagger extends JCasAnnotator_ImplBase {

	public static final String PARAM_TARGET_POS_CATEGORIES = "targetPosCategories";
	public static final String RESOURCE_WFSTORE = "WordformStore";
	public static final String RESOURCE_MORPH_DICTIONARY = "MorphDictionary";

	@ExternalResource(key = RESOURCE_MORPH_DICTIONARY)
	private MorphDictionaryHolder dictHolder;
	@ExternalResource(key = RESOURCE_WFSTORE, mandatory = true)
	private WordformStore wfStore;
	private AnnotationAdapter wordAnnoAdapter;
	@ConfigurationParameter(name = PARAM_TARGET_POS_CATEGORIES, mandatory = true)
	private String[] targetPosCategories;
	// derived
	private MorphDictionary dict;
	private PosTrimmer posTrimmer;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		dict = dictHolder.getDictionary();
		//
		wordAnnoAdapter = new DefaultAnnotationAdapter();
		wordAnnoAdapter.init(dict);
		//
		posTrimmer = new PosTrimmer(dict, targetPosCategories);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (W token : JCasUtil.select(jCas, W.class)) {
			String tokenStr = token.getCoveredText();
			tokenStr = WordUtils.normalizeToDictionaryForm(tokenStr);
			Set<BitSet> dictEntries = trimAndMergePosBits(dict.getEntries(tokenStr));
			if (dictEntries == null || dictEntries.isEmpty()) {
				setNotDictionary(jCas, addCasWordform(jCas, token));
			} else if (dictEntries.size() == 1) {
				wordAnnoAdapter.apply(jCas, token, null, null, dictEntries.iterator().next());
			} else {
				BitSet posBits = wfStore.getPosBits(tokenStr);
				if (posBits != null) {
					wordAnnoAdapter.apply(jCas, token, null, null, posBits);
				} else {
					setAmbiguous(jCas, addCasWordform(jCas, token));
				}
			}
		}
	}

	private Set<BitSet> trimAndMergePosBits(Iterable<Wordform> dictEntries) {
		if (dictEntries == null) {
			return null;
		}
		Iterable<BitSet> bsDictEntries = Iterables
				.transform(dictEntries, allGramBitsFunction(dict));
		return posTrimmer.trimAndMerge(bsDictEntries);
	}

	private org.opencorpora.cas.Wordform addCasWordform(JCas jCas, Annotation tokenAnno) {
		Word word = new Word(jCas);
		word.setBegin(tokenAnno.getBegin());
		word.setEnd(tokenAnno.getEnd());
		word.setToken(tokenAnno);
		org.opencorpora.cas.Wordform casWf = new org.opencorpora.cas.Wordform(jCas);
		casWf.setWord(word);
		word.setWordforms(FSUtils.toFSArray(jCas, casWf));
		//
		word.addToIndexes();
		//
		return casWf;
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