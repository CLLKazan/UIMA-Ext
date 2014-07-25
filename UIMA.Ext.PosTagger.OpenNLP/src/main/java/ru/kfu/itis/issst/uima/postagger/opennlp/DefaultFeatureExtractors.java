/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static ru.ksu.niimm.cll.uima.morph.ml.DefaultFeatureExtractors.contextTokenExtractors;
import static ru.ksu.niimm.cll.uima.morph.ml.DefaultFeatureExtractors.currentTokenExtractors;

import java.util.List;
import java.util.Properties;

import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Context;
import org.cleartk.classifier.feature.extractor.simple.CombinedExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
// TODO eliminate this inheritance
public class DefaultFeatureExtractors extends FeatureExtractorsBasedContextGenerator {

	public static final String PROP_DICTIONARY_VERSION = "dictionary.version";

	public static DefaultFeatureExtractors from(Properties props, MorphDictionary morphDict) {
		int prevTagsInHistory = ConfigPropertiesUtils.getIntProperty(props, "prevTags");
		int leftContextSize = ConfigPropertiesUtils.getIntProperty(props, "leftContext");
		int rightContextSize = ConfigPropertiesUtils.getIntProperty(props, "rightContext");
		if (morphDict != null) {
			String morphDictVersion = ConfigPropertiesUtils.getStringProperty(props,
					PROP_DICTIONARY_VERSION);
			if (!Objects.equal(morphDictVersion, morphDict.getVersion())) {
				throw new IllegalStateException(String.format(
						"Dictionary versions do not match:\n"
								+ "The feature extractors have %s\n"
								+ "The supplied dictionary has %s",
						morphDictVersion, morphDict.getVersion()));
			}
		}
		String targetGramCategoriesStr = ConfigPropertiesUtils.getStringProperty(
				props, "targetGramCategories");
		Iterable<String> targetGramCategories = Splitter.on(',').trimResults()
				.split(targetGramCategoriesStr);
		return new DefaultFeatureExtractors(prevTagsInHistory, leftContextSize, rightContextSize,
				targetGramCategories, morphDict);
	}

	public static void to(DefaultFeatureExtractors obj, Properties props) {
		props.setProperty("prevTags", String.valueOf(obj.getPrevTagsInHistory()));
		props.setProperty("leftContext", String.valueOf(obj.leftContextSize));
		props.setProperty("rightContext", String.valueOf(obj.rightContextSize));
		props.setProperty("targetGramCategories",
				Joiner.on(',').join(obj.getTargetGramCategories()));
		if (obj.getMorphDict() != null) {
			props.setProperty(PROP_DICTIONARY_VERSION, obj.getMorphDict().getVersion());
		}
	}

	private int leftContextSize;
	private int rightContextSize;

	public DefaultFeatureExtractors(int prevTagsInHistory,
			int leftContextSize, int rightContextSize,
			Iterable<String> targetGramCategories,
			MorphDictionary morphDict) {
		super(prevTagsInHistory, defaultExtractors(leftContextSize, rightContextSize),
				targetGramCategories, morphDict);
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
		feList.addAll(currentTokenExtractors());

		List<SimpleFeatureExtractor> ctxTokenFeatureExtractors = contextTokenExtractors();

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
		feList.add(new CleartkExtractor(Token.class,
				new CombinedExtractor(ctxTokenFeatureExtractors
						.toArray(new SimpleFeatureExtractor[0])),
				contexts.toArray(new Context[contexts.size()])));
		return feList;
	}
}
