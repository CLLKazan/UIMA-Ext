/**
 *
 */
package ru.kfu.itis.issst.uima.ml;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static ru.kfu.itis.cll.uima.util.BitUtils.contains;
import static ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryUtils.toGramBits;
import static ru.kfu.itis.issst.uima.morph.model.Wordform.allGramBitsFunction;

import java.util.*;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.NamedFeatureExtractor1;
import org.opencorpora.cas.Word;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.WordUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.model.Grammeme;
import ru.kfu.itis.issst.uima.morph.model.Wordform;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev
 *
 */
public class DictionaryPossibleTagFeatureExtractor implements NamedFeatureExtractor1 {

	/**
	 * this name will only be used by CleartkExtractor if target is
	 * out-of-bounds
	 */
	public static final String FEATURE_NAME = "DictTags";

	// config fields
	private MorphDictionary morphDict;
	private GramModel gramModel;
	// derived
	private final Map<Grammeme, BitSet> targetTagCategoriesMap;
	// TODO make targetCategoriesMask immutable!
	private final BitSet targetCategoriesMask;
	private final BitSet availableCategoriesMask;
	private final String baseFeatureName;

	public DictionaryPossibleTagFeatureExtractor(Iterable<String> targetTagCategories,
			Iterable<String> availableTagCategories,
			MorphDictionary morphDict) {
		this.morphDict = morphDict;
		this.gramModel = morphDict.getGramModel();
		// re-pack into a set to avoid duplicates and maintain ID-based ordering
		TreeSet<Grammeme> tagCatGrams = Sets.newTreeSet(Grammeme.numIdComparator());
		for (String tc : targetTagCategories) {
			Grammeme tcGram = gramModel.getGrammem(tc);
			if (tcGram == null) {
				throw new IllegalArgumentException(String.format(
						"Tag category %s does not exist", tc));
			}
			tagCatGrams.add(tcGram);
		}
		this.targetTagCategoriesMap = Maps.newHashMapWithExpectedSize(tagCatGrams.size());
		this.targetCategoriesMask = new BitSet();
		StringBuilder baseFeatureNameBuilder = new StringBuilder(FEATURE_NAME);
		for (Grammeme tcg : tagCatGrams) {
			BitSet tcBits = gramModel.getGrammemWithChildrenBits(tcg.getId(), true);
			targetTagCategoriesMap.put(tcg, tcBits);
			targetCategoriesMask.or(tcBits);
			baseFeatureNameBuilder.append('_').append(tcg.getId());
		}
		this.baseFeatureName = baseFeatureNameBuilder.toString();
		//
		if (availableTagCategories == null) {
			availableTagCategories = ImmutableList.of();
		}
		this.availableCategoriesMask = new BitSet();
		for (String posCat : availableTagCategories) {
			BitSet posCatBits = gramModel.getGrammemWithChildrenBits(posCat, true);
			if (posCatBits == null) {
				throw new IllegalStateException(String.format(
						"Grammeme %s does not exist!", posCat));
			}
			availableCategoriesMask.or(posCatBits);
		}
	}

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {
		Word focusWord = MLPackageUtils.getWordAnno(view, focusAnnotation);
		if (focusWord == null || focusWord.getWordforms() == null) {
			return ImmutableList.of();
		}
		String form = focusWord.getCoveredText();
		if (!WordUtils.isRussianWord(form)) {
			return ImmutableList.of(new Feature(FEATURE_NAME, "NotRussian"));
		}
		form = WordUtils.normalizeToDictionaryForm(form);
		List<Wordform> dictWfs = morphDict.getEntries(form);
		if (dictWfs == null || dictWfs.isEmpty()) {
			return ImmutableList.of(new Feature(FEATURE_NAME, "Unknown"));
		}
		List<BitSet> dictWfBitSets = Lists.transform(dictWfs,
				allGramBitsFunction(morphDict));
		//
		org.opencorpora.cas.Wordform focusWf = focusWord.getWordforms(0);
		BitSet focusWfBits = toGramBits(gramModel, FSUtils.toList(focusWf.getGrammems()));
		focusWfBits.and(availableCategoriesMask);
		//
		Set<BitSet> tokenPossibleTags = newHashSetWithExpectedSize(dictWfBitSets.size());
		for (BitSet dictWfBits : dictWfBitSets) {
			if (!contains(dictWfBits, focusWfBits)) {
				continue;
			}
			BitSet tokenPossibleBits = (BitSet) dictWfBits.clone();
			tokenPossibleBits.and(targetCategoriesMask);
			tokenPossibleTags.add(tokenPossibleBits);
		}
		List<Feature> resultList = newArrayListWithExpectedSize(tokenPossibleTags.size());
		for (BitSet tokenPossibleBits : tokenPossibleTags) {
			String featValue;
			if (tokenPossibleBits.isEmpty()) {
				featValue = "NULL";
			} else {
				featValue = gramJoiner.join(gramModel.toGramSet(tokenPossibleBits));
			}
			resultList.add(new Feature(baseFeatureName, featValue));
		}
		return resultList;
	}

    public List<Feature> extract(String form, Collection<String> availableTokenGrams)
            throws CleartkExtractorException {
        if (!WordUtils.isRussianWord(form)) {
            return ImmutableList.of(new Feature(FEATURE_NAME, "NotRussian"));
        }
        form = WordUtils.normalizeToDictionaryForm(form);
        List<Wordform> dictWfs = morphDict.getEntries(form);
        if (dictWfs == null || dictWfs.isEmpty()) {
            return ImmutableList.of(new Feature(FEATURE_NAME, "Unknown"));
        }
        List<BitSet> dictWfBitSets = Lists.transform(dictWfs, allGramBitsFunction(morphDict));
        //
        BitSet focusWfBits = toGramBits(gramModel, availableTokenGrams);
        //
        Set<BitSet> tokenPossibleTags = newHashSetWithExpectedSize(dictWfBitSets.size());
        for (BitSet dictWfBits : dictWfBitSets) {
            if (!contains(dictWfBits, focusWfBits)) {
                // this dictionary entry is not compatible with token current grams
                continue;
            }
            BitSet tokenPossibleBits = (BitSet) dictWfBits.clone();
            tokenPossibleBits.and(targetCategoriesMask);
            tokenPossibleTags.add(tokenPossibleBits);
        }
        List<Feature> resultList = newArrayListWithExpectedSize(tokenPossibleTags.size());
        for (BitSet tokenPossibleBits : tokenPossibleTags) {
            String featValue;
            if (tokenPossibleBits.isEmpty()) {
                featValue = "NULL";
            } else {
                featValue = gramJoiner.join(gramModel.toGramSet(tokenPossibleBits));
            }
            resultList.add(new Feature(baseFeatureName, featValue));
        }
        return resultList;
    }

	private static final Joiner gramJoiner = Joiner.on('_');

	@Override
	public String getFeatureName() {
		return FEATURE_NAME;
	}
}