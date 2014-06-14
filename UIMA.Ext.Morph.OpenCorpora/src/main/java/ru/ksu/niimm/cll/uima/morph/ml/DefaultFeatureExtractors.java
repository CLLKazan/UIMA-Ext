/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.util.List;

import org.cleartk.classifier.feature.extractor.simple.CoveredTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction.Orientation;
import org.cleartk.classifier.feature.function.FeatureFunctionExtractor;
import org.cleartk.classifier.feature.function.LowerCaseFeatureFunction;
import org.cleartk.classifier.feature.function.NumericTypeFeatureFunction;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultFeatureExtractors {

	public static List<SimpleFeatureExtractor> currentTokenExtractors() {
		return Lists.<SimpleFeatureExtractor> newArrayList(new FeatureFunctionExtractor(
				new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new CapitalTypeFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3, 4, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 2, 3, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 1, 2, true)));
	}

	public static List<SimpleFeatureExtractor> contextTokenExtractors() {
		return Lists.<SimpleFeatureExtractor> newArrayList(new FeatureFunctionExtractor(
				new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3, 4, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 2, 3, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 1, 2, true)));
	}

	private DefaultFeatureExtractors() {
	}
}
