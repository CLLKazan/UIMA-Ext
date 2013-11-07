/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleNamedFeatureExtractor;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev
 * 
 */
public class SuffixFeatureExtractor implements SimpleNamedFeatureExtractor {

	private int suffixLength;
	// derived
	private final String featureName;

	public SuffixFeatureExtractor(int suffixLength) {
		if (suffixLength <= 0) {
			throw new IllegalArgumentException();
		}
		this.suffixLength = suffixLength;
		featureName = Feature.createName("Suffix", String.valueOf(suffixLength));
	}

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public List<Feature> extract(JCas view, Annotation focusAnnotation)
			throws CleartkExtractorException {

		String str = focusAnnotation.getCoveredText();
		String val;
		if (str.length() <= suffixLength) {
			val = str;
		} else {
			val = "*" + str.substring(str.length() - suffixLength);
		}
		return ImmutableList.of(new Feature(featureName, val));
	}
}