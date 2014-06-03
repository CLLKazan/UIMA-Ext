/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.List;
import java.util.Properties;

import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Context;
import org.cleartk.classifier.feature.extractor.simple.CoveredTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction.Orientation;
import org.cleartk.classifier.feature.function.FeatureFunctionExtractor;
import org.cleartk.classifier.feature.function.LowerCaseFeatureFunction;
import org.cleartk.classifier.feature.function.NumericTypeFeatureFunction;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultFeatureExtractors extends FeatureExtractorsBasedContextGenerator {

	public static DefaultFeatureExtractors from(Properties props) {
		int prevTagsInHistory = ConfigPropertiesUtils.getIntProperty(props, "prevTags");
		int leftContextSize = ConfigPropertiesUtils.getIntProperty(props, "leftContext");
		int rightContextSize = ConfigPropertiesUtils.getIntProperty(props, "rightContext");
		return new DefaultFeatureExtractors(prevTagsInHistory, leftContextSize, rightContextSize);
	}

	public static void to(DefaultFeatureExtractors obj, Properties props) {
		props.setProperty("prevTags", String.valueOf(obj.getPrevTagsInHistory()));
		props.setProperty("leftContext", String.valueOf(obj.leftContextSize));
		props.setProperty("rightContext", String.valueOf(obj.rightContextSize));
	}

	private int leftContextSize;
	private int rightContextSize;

	public DefaultFeatureExtractors(int prevTagsInHistory,
			int leftContextSize, int rightContextSize) {
		super(prevTagsInHistory, defaultExtractors(leftContextSize, rightContextSize));
		this.leftContextSize = leftContextSize;
		this.rightContextSize = rightContextSize;
	}

	public int getLeftContextSize() {
		return leftContextSize;
	}

	public int getRightContextSize() {
		return rightContextSize;
	}

	public static List<SimpleFeatureExtractor> defaultExtractors(int leftContextSize,
			int rightContextSize) {
		List<SimpleFeatureExtractor> feList = Lists.newLinkedList();
		feList.add(new FeatureFunctionExtractor(
				new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new CapitalTypeFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3, 4, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 2, 3, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 1, 2, true)));

		FeatureFunctionExtractor ctxTokenFeatureExtractor = new FeatureFunctionExtractor(
				new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3, 4, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 2, 3, true),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 1, 2, true));

		// TODO introduce difference between Null and NotApplicable values

		if (leftContextSize < 0 || rightContextSize < 0) {
			throw new IllegalStateException("context size < 0");
		}
		if (leftContextSize == 0 && rightContextSize == 0) {
			throw new IllegalStateException("left & right context sizes == 0");
		}
		List<Context> contexts = Lists.newArrayList();
		if (leftContextSize > 0) {
			contexts.add(new CleartkExtractor.Preceding(leftContextSize));
		}
		if (rightContextSize > 0) {
			contexts.add(new CleartkExtractor.Following(rightContextSize));
		}
		feList.add(new CleartkExtractor(Token.class, ctxTokenFeatureExtractor,
				contexts.toArray(new Context[contexts.size()])));
		return feList;
	}
}
