/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev
 * 
 */
public class SuffixFeatureExtractor implements SimpleFeatureExtractor {

	private int maxSuffixLength;

	public SuffixFeatureExtractor(int maxSuffixLength) {
		if (maxSuffixLength <= 0) {
			throw new IllegalArgumentException();
		}
		this.maxSuffixLength = maxSuffixLength;
	}

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {

		String str = focusAnnotation.getCoveredText();
		List<Feature> result = Lists.newLinkedList();
		for (int suffixLength = 1; suffixLength <= maxSuffixLength; suffixLength++) {
			String val;
			if (str.length() <= suffixLength) {
				val = str;
			} else {
				val = "*" + str.substring(str.length() - suffixLength);
			}
			result.add(new Feature(getFeatureName(suffixLength), val));
			if (str.length() <= suffixLength) {
				// suffix length increasing so there is no point to produce other features with different name
				break;
			}
		}
		return result;
	}

	private String getFeatureName(int suffixLength) {
		return Feature.createName("Suffix", String.valueOf(suffixLength));
	}
}