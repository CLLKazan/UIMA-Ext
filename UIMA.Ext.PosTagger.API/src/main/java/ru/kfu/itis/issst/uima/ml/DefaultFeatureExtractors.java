/**
 * 
 */
package ru.kfu.itis.issst.uima.ml;

import java.util.List;

import org.cleartk.ml.feature.extractor.CoveredTextExtractor;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.cleartk.ml.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.ml.feature.function.CharacterNgramFeatureFunction;
import org.cleartk.ml.feature.function.CharacterNgramFeatureFunction.Orientation;
import org.cleartk.ml.feature.function.FeatureFunctionExtractor;
import org.cleartk.ml.feature.function.LowerCaseFeatureFunction;
import org.cleartk.ml.feature.function.NumericTypeFeatureFunction;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultFeatureExtractors {

	public static List<FeatureExtractor1> currentTokenExtractors() {
		return Lists.<FeatureExtractor1> newArrayList(new FeatureFunctionExtractor(
				new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new CapitalTypeFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNgramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3, 4, true),
				new CharacterNgramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 2, 3, true),
				new CharacterNgramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 1, 2, true)));
	}

	public static List<FeatureExtractor1> contextTokenExtractors() {
		return Lists.<FeatureExtractor1> newArrayList(new FeatureFunctionExtractor(
				new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNgramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3, 4, true),
				new CharacterNgramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 2, 3, true),
				new CharacterNgramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 1, 2, true)));
	}

	private DefaultFeatureExtractors() {
	}
}
