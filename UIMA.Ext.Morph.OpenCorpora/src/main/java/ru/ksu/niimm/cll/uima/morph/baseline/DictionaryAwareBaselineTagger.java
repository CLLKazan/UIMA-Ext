/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.util.BitSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.ksu.niimm.cll.uima.morph.opencorpora.AnnotationAdapter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.DefaultAnnotationAdapter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

/**
 * Note that if baseline model is trained on a 'PosTrimmed' corpus then
 * {@link PosTrimmer} should be applied afterwards to align PoS-tags between the
 * training corpus and a supplied dictionary.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryAwareBaselineTagger extends JCasAnnotator_ImplBase {

	public static final String RESOURCE_WFSTORE = "WordformStore";
	public static final String RESOURCE_MORPH_DICTIONARY = "MorphDictionary";

	@ExternalResource(key = RESOURCE_MORPH_DICTIONARY)
	private MorphDictionaryHolder dictHolder;
	@ExternalResource(key = RESOURCE_WFSTORE, mandatory = true)
	private WordformStore wfStore;
	private AnnotationAdapter wordAnnoAdapter;
	// derived
	private MorphDictionary dict;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		dict = dictHolder.getDictionary();
		wordAnnoAdapter = new DefaultAnnotationAdapter();
		wordAnnoAdapter.init(dict);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		// TODO what about complex tokens? Look at PostTokenizer
		for (W token : JCasUtil.select(jCas, W.class)) {
			String tokenStr = token.getCoveredText();
			tokenStr = WordUtils.normalizeToDictionaryForm(tokenStr);
			List<Wordform> dictEntries = dict.getEntries(tokenStr);
			if (dictEntries == null) {
				continue;
			} else if (dictEntries.size() == 1) {
				wordAnnoAdapter.apply(jCas, token, dictEntries);
			} else {
				BitSet posBits = wfStore.getPosBits(tokenStr);
				if (posBits != null) {
					wordAnnoAdapter.apply(jCas, token, null, null, posBits);
				}
			}
		}
	}
}