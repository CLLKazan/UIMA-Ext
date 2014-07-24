/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.util.JCasUtil;

import ru.kfu.itis.issst.uima.morph.commons.AgreementPredicates;
import ru.kfu.itis.issst.uima.morph.commons.GramModelBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TwoTagPredicate;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Number-Gender-Case feature generator.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class NGCAgreementFeatureExtractor implements SimpleFeatureExtractor {

	private GramModel gramModel;
	private TagMapper tagMapper;
	//
	private Map<String, TwoTagPredicate> namedPredicates;

	public NGCAgreementFeatureExtractor(GramModel gramModel) {
		this.gramModel = gramModel;
		this.namedPredicates = AgreementPredicates.numberGenderCaseCombinations(gramModel);
		// TODO:LOW
		tagMapper = new GramModelBasedTagMapper(gramModel);
	}

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {
		Word focusWord = PUtils.getWordAnno(view, focusAnnotation);
		if (focusWord == null || focusWord.getWordforms() == null
				|| focusWord.getWordforms().size() == 0) {
			return ImmutableList.of();
		}
		Word precWord = getPrecedingWord(view, focusWord);
		if (precWord == null || precWord.getWordforms() == null
				|| precWord.getWordforms().size() == 0) {
			return ImmutableList.of();
		}
		Wordform curWf = (Wordform) focusWord.getWordforms().get(0);
		Wordform precWf = (Wordform) precWord.getWordforms().get(0);
		if (precWf == null) {
			return ImmutableList.of();
		}
		// to Bitset
		BitSet curTag = toGramBits(gramModel,
				tagMapper.parseTag(curWf.getPos(), focusWord.getCoveredText()));
		BitSet precTag = toGramBits(gramModel,
				tagMapper.parseTag(precWf.getPos(), precWord.getCoveredText()));
		// generate features
		List<Feature> result = Lists.newLinkedList();
		for (Map.Entry<String, TwoTagPredicate> npEntry : namedPredicates.entrySet()) {
			TwoTagPredicate predicate = npEntry.getValue();
			String predicateName = npEntry.getKey();
			if (predicate.apply(precTag, curTag)) {
				result.add(new Feature(predicateName, true));
			}
		}
		return result;
	}

	private Word getPrecedingWord(JCas jCas, Word curWord) {
		List<Word> precedingWords = JCasUtil.selectPreceding(jCas, Word.class, curWord, 1);
		if (!precedingWords.isEmpty()) {
			return precedingWords.get(0);
		} else {
			return null;
		}
	}
}
