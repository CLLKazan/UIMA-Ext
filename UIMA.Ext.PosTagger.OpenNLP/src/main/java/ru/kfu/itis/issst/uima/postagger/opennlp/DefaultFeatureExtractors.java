/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static ru.ksu.niimm.cll.uima.morph.ml.DefaultFeatureExtractors.contextTokenExtractors;
import static ru.ksu.niimm.cll.uima.morph.ml.DefaultFeatureExtractors.currentTokenExtractors;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Context;
import org.cleartk.classifier.feature.extractor.simple.CombinedExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils;

import com.google.common.base.Splitter;
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
		File morphDictFile = ConfigPropertiesUtils.getFileProperty(props, "morphDictFile", false);
		String targetGramCategoriesStr = ConfigPropertiesUtils.getStringProperty(
				props, "targetGramCategories");
		Iterable<String> targetGramCategories = Splitter.on(',').trimResults()
				.split(targetGramCategoriesStr);
		return new DefaultFeatureExtractors(prevTagsInHistory, leftContextSize, rightContextSize,
				morphDictFile, targetGramCategories);
	}

	public static void to(DefaultFeatureExtractors obj, Properties props) {
		props.setProperty("prevTags", String.valueOf(obj.getPrevTagsInHistory()));
		props.setProperty("leftContext", String.valueOf(obj.leftContextSize));
		props.setProperty("rightContext", String.valueOf(obj.rightContextSize));
	}

	private int leftContextSize;
	private int rightContextSize;

	public DefaultFeatureExtractors(int prevTagsInHistory,
			int leftContextSize, int rightContextSize,
			File morphDictFile, Iterable<String> targetGramCategories) {
		super(prevTagsInHistory, defaultExtractors(leftContextSize, rightContextSize),
				morphDictFile, targetGramCategories);
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
