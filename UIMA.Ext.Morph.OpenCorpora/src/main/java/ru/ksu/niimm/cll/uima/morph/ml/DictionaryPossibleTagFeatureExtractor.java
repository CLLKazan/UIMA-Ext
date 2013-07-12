/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleNamedFeatureExtractor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

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

	private Map<Grammeme, BitSet> tagCategoriesMap;
	private MorphDictionary morphDict;

	public DictionaryPossibleTagFeatureExtractor(List<String> tagCategories,
			MorphDictionary morphDict) {
		this.tagCategoriesMap = Maps.newHashMapWithExpectedSize(tagCategories.size());
		for (String tc : tagCategories) {
			Grammeme tcGram = morphDict.getGrammem(tc);
			if (tcGram == null) {
				throw new IllegalArgumentException(String.format(
						"Tag category %s does not exist", tc));
			}
			BitSet tcBits = morphDict.getGrammemWithChildrenBits(tcGram.getId(), true);
			if (tcBits == null) {
				throw new IllegalStateException(String.format(
						"%s does not have children grammems!", tcGram.getId()));
			}
			tagCategoriesMap.put(tcGram, tcBits);
		}
		this.morphDict = morphDict;
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
		List<BitSet> wfsTags = extractTags(wfs);
		List<Feature> resultList = Lists.newArrayListWithExpectedSize(tagCategoriesMap.size());
		for (Grammeme tcGram : tagCategoriesMap.keySet()) {
			String featName = tcGram.getId();
			BitSet catBits = tagCategoriesMap.get(tcGram);
			BitSet tokenPossibleBits = new BitSet(catBits.length());
			for (BitSet wfBits : wfsTags) {
				assignCategoryBits(tokenPossibleBits, wfBits, catBits);
			}
			String featValue;
			if (tokenPossibleBits.isEmpty()) {
				featValue = "NULL";
			} else {
				// TODO compress BitSet (using kind of << operator) and use resulting String representation
				StringBuilder fvBuilder = new StringBuilder();
				for (int i = tokenPossibleBits.nextSetBit(0); i >= 0; i = tokenPossibleBits
						.nextSetBit(i + 1)) {
					fvBuilder.append(morphDict.getGrammem(i).getId());
					fvBuilder.append('_');
				}
				fvBuilder.deleteCharAt(fvBuilder.length() - 1);
				featValue = fvBuilder.toString();
			}
			resultList.add(new Feature(featName, featValue));
		}
		return resultList;
	}

	@Override
	public String getFeatureName() {
		return FEATURE_NAME;
	}

	private void assignCategoryBits(BitSet targetBits, BitSet srcBits, BitSet catBits) {
		// assume that catBits usually smaller than srcBits
		for (int tagBit = catBits.nextSetBit(0); tagBit >= 0; tagBit = catBits
				.nextSetBit(tagBit + 1)) {
			if (srcBits.get(tagBit)) {
				targetBits.set(tagBit);
			}
		}
	}

	private List<BitSet> extractTags(List<Wordform> wfList) {
		List<BitSet> result = Lists.newArrayListWithCapacity(wfList.size());
		for (Wordform wf : wfList) {
			result.add(extractTags(wf));
		}
		return result;
	}

	private BitSet extractTags(Wordform wf) {
		BitSet result = wf.getGrammems();
		Lemma lemma = morphDict.getLemma(wf.getLemmaId());
		result.or(lemma.getGrammems());
		return result;
	}
}