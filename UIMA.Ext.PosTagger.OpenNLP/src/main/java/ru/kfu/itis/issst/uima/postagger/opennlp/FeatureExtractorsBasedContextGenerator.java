/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.List;
import java.util.Set;

import opennlp.tools.util.BeamSearchContextGenerator;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.encoder.CleartkEncoderException;
import org.cleartk.classifier.encoder.features.FeatureEncoderChain;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.cleartk.DefaultFeatureToStringEncoderChain;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FeatureExtractorsBasedContextGenerator implements BeamSearchContextGenerator<Token> {

	private final int prevTagsInHistory;
	private List<SimpleFeatureExtractor> featureExtractors;
	private FeatureEncoderChain<String> featureEncoders = new DefaultFeatureToStringEncoderChain();
	private Set<String> targetGramCategories;
	private MorphDictionary morphDict;
	//
	private DictionaryBasedContextGenerator dictContextGen;

	public FeatureExtractorsBasedContextGenerator(int prevTagsInHistory,
			List<SimpleFeatureExtractor> featureExtractors,
			Iterable<String> targetGramCategories,
			MorphDictionary morphDict) {
		this.prevTagsInHistory = prevTagsInHistory;
		this.featureExtractors = ImmutableList.copyOf(featureExtractors);
		this.targetGramCategories = Sets.newLinkedHashSet(targetGramCategories);
		this.morphDict = morphDict;
		if (this.morphDict != null) {
			dictContextGen = new DictionaryBasedContextGenerator(targetGramCategories, morphDict);
		}
	}

	public int getPrevTagsInHistory() {
		return prevTagsInHistory;
	}

	public Iterable<String> getTargetGramCategories() {
		return targetGramCategories;
	}

	public MorphDictionary getMorphDict() {
		return morphDict;
	}

	@Override
	public String[] getContext(int index, Token[] sequence, String[] priorDecisions,
			Object[] additionalContext) {
		if (additionalContext == null || additionalContext.length < 1) {
			throw sentenceExpected();
		}
		if (!(additionalContext[0] instanceof Annotation)) {
			throw sentenceExpected();
		}
		Annotation sent = (Annotation) additionalContext[0];
		// TODO cache features that does not dependent on prev tags 
		Token curToken = sequence[index];
		List<Feature> features = Lists.newLinkedList();
		try {
			JCas jCas = curToken.getCAS().getJCas();
			for (SimpleFeatureExtractor fe : featureExtractors) {
				if (fe instanceof CleartkExtractor) {
					features.addAll(((CleartkExtractor) fe).extractBetween(jCas, curToken, sent));
				} else {
					features.addAll(fe.extract(jCas, curToken));
				}
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

	private RuntimeException sentenceExpected() {
		return new IllegalArgumentException(
				"Sentence annotation is expected to be provided in 'additionalContext' arg");
	}
}
