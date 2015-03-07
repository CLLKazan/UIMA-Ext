/**
 * 
 */
package ru.kfu.itis.issst.uima.ml;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.NamedFeatureExtractor1;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.apache.uima.fit.util.FSCollectionFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTagFeatureExtractor implements NamedFeatureExtractor1 {

	public static final String FEATURE_NAME = "PosTag";

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {
		Word focusWord = PUtils.getWordAnno(view, focusAnnotation);
		if (focusWord == null || focusWord.getWordforms() == null) {
			return ImmutableList.of();
		}
		Collection<Wordform> wfs = FSCollectionFactory.create(focusWord.getWordforms(),
				Wordform.class);
		Set<String> tags = Sets.newHashSet();
		for (Wordform wf : wfs) {
			tags.add(wf.getPos());
		}
		return Lists.newArrayList(Collections2.transform(tags, tag2Feature));
	}

	@Override
	public String getFeatureName() {
		return FEATURE_NAME;
	}

	private static final Function<String, Feature> tag2Feature = new Function<String, Feature>() {
		@Override
		public Feature apply(String tag) {
			return new Feature(FEATURE_NAME, tag);
		}
	};
}
