package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform.allGramBitsFunction;

import java.util.BitSet;
import java.util.Set;

import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.Iterables;

abstract class DictionaryAwareBaselineAnnotator extends BaselineAnnotator {
	
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
