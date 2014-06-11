package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform.allGramBitsFunction;

import java.util.BitSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ExternalResource;

import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

import com.google.common.collect.Iterables;

abstract class DictionaryAwareBaselineAnnotator extends JCasAnnotator_ImplBase {

	public static final String RESOURCE_MORPH_DICTIONARY = "MorphDictionary";

	// config fields
	@ExternalResource(key = RESOURCE_MORPH_DICTIONARY)
	private MorphDictionaryHolder dictHolder;
	// derived
	protected MorphDictionary dict;
	protected GramModel gramModel;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		dict = dictHolder.getDictionary();
		gramModel = dict.getGramModel();
	}

	public static final String PARAM_TARGET_POS_CATEGORIES = "targetPosCategories";

	protected PosTrimmer posTrimmer;

	protected Set<BitSet> trimAndMergePosBits(Iterable<Wordform> dictEntries) {
		if (dictEntries == null) {
			return null;
		}
		Iterable<BitSet> bsDictEntries = Iterables
				.transform(dictEntries, allGramBitsFunction(dict));
		return posTrimmer.trimAndMerge(bsDictEntries);
	}

}
