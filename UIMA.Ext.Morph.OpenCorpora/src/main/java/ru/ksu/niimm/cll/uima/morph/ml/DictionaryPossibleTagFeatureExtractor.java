/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleNamedFeatureExtractor;

import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev
 * 
 */
public class DictionaryPossibleTagFeatureExtractor implements SimpleNamedFeatureExtractor {

	/**
	 * this name will only be used by CleartkExtractor if target is
	 * out-of-bounds
	 */
	public static final String FEATURE_NAME = "DictTags";

	// config fields
	private MorphDictionary morphDict;
	// derived
	private final Map<Grammeme, BitSet> tagCategoriesMap;
	// TODO make filterBS immutable!
	private final BitSet filterBS;
	private final String baseFeatureName;

	public DictionaryPossibleTagFeatureExtractor(Iterable<String> tagCategories,
			MorphDictionary morphDict) {
		// re-pack into a set to avoid duplicates and maintain ID-based ordering
		TreeSet<Grammeme> tagCatGrams = Sets.newTreeSet(Grammeme.numIdComparator());
		for (String tc : tagCategories) {
			Grammeme tcGram = morphDict.getGrammem(tc);
			if (tcGram == null) {
				throw new IllegalArgumentException(String.format(
						"Tag category %s does not exist", tc));
			}
			tagCatGrams.add(tcGram);
		}
		this.tagCategoriesMap = Maps.newHashMapWithExpectedSize(tagCatGrams.size());
		this.filterBS = new BitSet();
		StringBuilder baseFeatureNameBuilder = new StringBuilder(FEATURE_NAME);
		for (Grammeme tcg : tagCatGrams) {
			BitSet tcBits = morphDict.getGrammemWithChildrenBits(tcg.getId(), true);
			if (tcBits == null) {
				throw new IllegalStateException(String.format(
						"%s does not have children grammems!", tcg.getId()));
			}
			tagCategoriesMap.put(tcg, tcBits);
			filterBS.or(tcBits);
			baseFeatureNameBuilder.append('_').append(tcg.getId());
		}
		this.morphDict = morphDict;
		this.baseFeatureName = baseFeatureNameBuilder.toString();
	}

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {
		String form = focusAnnotation.getCoveredText();
		if (!WordUtils.isRussianWord(form)) {
			return ImmutableList.of(new Feature(FEATURE_NAME, "NotRussian"));
		}
		form = WordUtils.normalizeToDictionaryForm(form);
		List<Wordform> wfs = morphDict.getEntries(form);
		if (wfs == null || wfs.isEmpty()) {
			return ImmutableList.of(new Feature(FEATURE_NAME, "Unknown"));
		}
		List<BitSet> wfsTags = Lists.transform(wfs, Wordform.allGramBitsFunction(morphDict));
		Set<BitSet> tokenPossibleTags = Sets.newHashSetWithExpectedSize(wfsTags.size());
		for (BitSet wfTagBits : wfsTags) {
			BitSet tokenPossibleBits = (BitSet) wfTagBits.clone();
			tokenPossibleBits.and(filterBS);
			tokenPossibleTags.add(tokenPossibleBits);
		}
		List<Feature> resultList = Lists.newArrayListWithExpectedSize(tokenPossibleTags.size());
		for (BitSet tokenPossibleBits : tokenPossibleTags) {
			String featValue;
			if (tokenPossibleBits.isEmpty()) {
				featValue = "NULL";
			} else {
				featValue = gramJoiner.join(morphDict.toGramSet(tokenPossibleBits));
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