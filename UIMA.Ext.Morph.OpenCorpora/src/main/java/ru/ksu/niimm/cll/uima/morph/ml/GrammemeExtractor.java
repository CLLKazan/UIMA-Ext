/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GrammemeExtractor implements SimpleFeatureExtractor {

	public static final String FEATURE_NAME_PREFIX = "Gram";

	@SuppressWarnings("unused")
	private String gramCat;
	private MorphDictionary dict;
	// TODO make filterBS immutable
	private BitSet filterBS;
	private String featureName;

	public GrammemeExtractor(MorphDictionary dict, String gramCat) {
		this.gramCat = gramCat;
		this.dict = dict;
		filterBS = dict.getGrammemWithChildrenBits(gramCat, true);
		featureName = FEATURE_NAME_PREFIX + "_" + gramCat;
	}

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {
		Word wordAnno = null;
		if (focusAnnotation instanceof Word) {
			wordAnno = (Word) focusAnnotation;
		} else if (focusAnnotation instanceof Token) {
			List<Word> wordsCovered = JCasUtil.selectCovered(Word.class, focusAnnotation);
			if (!wordsCovered.isEmpty()) {
				wordAnno = wordsCovered.get(0);
			}
		} else {
			throw CleartkExtractorException.wrongAnnotationType(Word.class, focusAnnotation);
		}
		if (wordAnno == null || wordAnno.getWordforms() == null) {
			return ImmutableList.of();
		}
		Collection<Wordform> wfs = FSCollectionFactory.create(
				wordAnno.getWordforms(), Wordform.class);
		if (wfs.isEmpty()) {
			return ImmutableList.of();
		}
		Wordform wf = wfs.iterator().next();
		BitSet wfBits = toGramBits(dict, FSUtils.toList(wf.getGrammems()));
		wfBits.and(filterBS);
		List<Feature> result = Lists.newArrayList();
		for (int i = wfBits.nextSetBit(0); i >= 0; i = wfBits.nextSetBit(i + 1)) {
			result.add(new Feature(featureName, dict.getGrammem(i).getId()));
		}
		return result;
	}
}