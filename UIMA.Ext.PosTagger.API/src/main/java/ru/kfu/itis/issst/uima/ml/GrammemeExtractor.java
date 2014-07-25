/**
 * 
 */
package ru.kfu.itis.issst.uima.ml;

import static ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryUtils.toGramBits;

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

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;

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
	private GramModel gramModel;
	// TODO make filterBS immutable
	private BitSet filterBS;
	private String featureName;

	public GrammemeExtractor(GramModel gramModel, String gramCat) {
		this.gramModel = gramModel;
		this.gramCat = gramCat;
		filterBS = gramModel.getGrammemWithChildrenBits(gramCat, true);
		featureName = FEATURE_NAME_PREFIX + "_" + gramCat;
	}

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {
		Word wordAnno = PUtils.getWordAnno(view, focusAnnotation);
		if (wordAnno == null || wordAnno.getWordforms() == null) {
			return ImmutableList.of();
		}
		Collection<Wordform> wfs = FSCollectionFactory.create(
				wordAnno.getWordforms(), Wordform.class);
		if (wfs.isEmpty()) {
			return ImmutableList.of();
		}
		Wordform wf = wfs.iterator().next();
		BitSet wfBits = toGramBits(gramModel, FSUtils.toList(wf.getGrammems()));
		wfBits.and(filterBS);
		List<Feature> result = Lists.newArrayList();
		for (int i = wfBits.nextSetBit(0); i >= 0; i = wfBits.nextSetBit(i + 1)) {
			result.add(new Feature(featureName, gramModel.getGrammem(i).getId()));
		}
		return result;
	}
}