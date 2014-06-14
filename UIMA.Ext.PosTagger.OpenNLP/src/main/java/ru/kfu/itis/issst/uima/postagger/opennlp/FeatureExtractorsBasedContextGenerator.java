/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.File;
import java.util.List;
import java.util.Set;

import opennlp.tools.util.BeamSearchContextGenerator;

import org.apache.uima.jcas.JCas;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.encoder.CleartkEncoderException;
import org.cleartk.classifier.encoder.features.FeatureEncoderChain;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.cleartk.DefaultFeatureToStringEncoderChain;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedDictionaryDeserializer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedDictionaryDeserializer.GetDictionaryResult;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FeatureExtractorsBasedContextGenerator implements BeamSearchContextGenerator<Token> {

	private final int prevTagsInHistory;
	protected final File morphDictFile;
	private List<SimpleFeatureExtractor> featureExtractors;
	private FeatureEncoderChain<String> featureEncoders = new DefaultFeatureToStringEncoderChain();
	//
	private GetDictionaryResult morphDictKey;
	private MorphDictionary morphDictionary;
	private DictionaryBasedContextGenerator dictContextGen;

	public FeatureExtractorsBasedContextGenerator(int prevTagsInHistory,
			List<SimpleFeatureExtractor> featureExtractors,
			File morphDictFile,
			Iterable<String> targetGramCategories) {
		this.prevTagsInHistory = prevTagsInHistory;
		this.morphDictFile = morphDictFile;
		this.featureExtractors = ImmutableList.copyOf(featureExtractors);
		if (morphDictFile != null) {
			try {
				morphDictKey = CachedDictionaryDeserializer.getInstance().getDictionary(
						morphDictFile);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			morphDictionary = morphDictKey.dictionary;
			dictContextGen = new DictionaryBasedContextGenerator(targetGramCategories,
					morphDictionary);
		}
	}

	public int getPrevTagsInHistory() {
		return prevTagsInHistory;
	}

	@Override
	public String[] getContext(int index, Token[] sequence, String[] priorDecisions,
			Object[] additionalContext) {
		// TODO cache features that does not dependent on prev tags 
		Token curToken = sequence[index];
		List<Feature> features = Lists.newLinkedList();
		try {
			JCas jCas = curToken.getCAS().getJCas();
			for (SimpleFeatureExtractor fe : featureExtractors) {
				features.addAll(fe.extract(jCas, curToken));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// encode
		Set<String> contexts = Sets.newLinkedHashSetWithExpectedSize(
				features.size() + prevTagsInHistory);
		// TODO move to utils
		for (Feature f : features) {
			try {
				contexts.addAll(featureEncoders.encode(f));
			} catch (CleartkEncoderException e) {
				throw new RuntimeException(e);
			}
		}
		ContextGeneratorUtils.addPreviousTags(index, priorDecisions, prevTagsInHistory, contexts);
		if (dictContextGen != null) {
			String prevTag = ContextGeneratorUtils.getPreviousTag(index, priorDecisions);
			contexts.addAll(dictContextGen.extract(curToken, prevTag));
		}
		return contexts.toArray(new String[contexts.size()]);
	}
}
