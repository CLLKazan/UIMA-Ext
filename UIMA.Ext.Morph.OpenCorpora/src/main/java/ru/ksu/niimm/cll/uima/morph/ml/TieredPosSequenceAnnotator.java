/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.Instances;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.simple.CombinedExtractor;
import org.cleartk.classifier.feature.extractor.simple.CoveredTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction.Orientation;
import org.cleartk.classifier.feature.function.FeatureFunctionExtractor;
import org.cleartk.classifier.feature.function.LowerCaseFeatureFunction;
import org.cleartk.classifier.feature.function.NumericTypeFeatureFunction;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TieredPosSequenceAnnotator extends CleartkSequenceAnnotator<String> {

	public static final String RESOURCE_KEY_MORPH_DICTIONARY = "MorphDictionary";
	public static final String PARAM_POS_TIERS = "posTiers";
	public static final String PARAM_CURRENT_TIER = "currentTier";
	// config fields
	@ExternalResource(key = RESOURCE_KEY_MORPH_DICTIONARY)
	private MorphDictionaryHolder morphDictHolder;
	@ConfigurationParameter(name = PARAM_POS_TIERS, mandatory = true)
	private List<String> pPosTiers;
	@ConfigurationParameter(name = PARAM_CURRENT_TIER, mandatory = true)
	private int currentTier = -1;
	// derived
	private MorphDictionary morphDictionary;
	private Set<String> currentPosTier;
	// TODO make filterBS immutable
	private BitSet filterBS;
	private List<Set<String>> posTiers;
	private Set<String> prevTierPosCategories;
	// features
	private SimpleFeatureExtractor tokenFeatureExtractor;
	private SimpleFeatureExtractor dictFeatureExtractor;
	private SimpleFeatureExtractor posExtractor;
	private CleartkExtractor contextFeatureExtractor;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		// validate tiers configuration
		if (currentTier < 0 || currentTier >= pPosTiers.size()) {
			throw new IllegalStateException(String.format(
					"Illegal current tier param value: %s", currentTier));
		}
		parsePosTiersParameter();
		morphDictionary = morphDictHolder.getDictionary();
		makeFilterBitSet();

		tokenFeatureExtractor = new FeatureFunctionExtractor(new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new CapitalTypeFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3));

		List<SimpleFeatureExtractor> gramExtractors = Lists.newArrayList();
		List<SimpleFeatureExtractor> contextFeatureExtractors = Lists.newArrayList();
		contextFeatureExtractors.add(new SuffixFeatureExtractor(3));
		for (String posCat : prevTierPosCategories) {
			GrammemeExtractor gramExtractor = new GrammemeExtractor(morphDictionary, posCat);
			gramExtractors.add(gramExtractor);
			contextFeatureExtractors.add(gramExtractor);
		}
		// TODO introduce difference between Null and NotApplicable values
		posExtractor = new CombinedExtractor(gramExtractors.toArray(FE_ARRAY));
		dictFeatureExtractor = new DictionaryPossibleTagFeatureExtractor(
				currentPosTier, morphDictionary);

		contextFeatureExtractor = new CleartkExtractor(Word.class,
				new CombinedExtractor(contextFeatureExtractors.toArray(FE_ARRAY)),
				new CleartkExtractor.Preceding(2), new CleartkExtractor.Following(2));
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			process(jCas, sent);
		}
	}

	private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		List<List<Feature>> sentSeq = Lists.newArrayList();
		List<String> sentLabels = null;
		if (isTraining()) {
			sentLabels = Lists.newArrayList();
		}
		for (Word word : JCasUtil.selectCovered(jCas, Word.class, sent)) {
			if (isTraining()) {
				FSArray tokWordforms = word.getWordforms();
				if (tokWordforms == null || tokWordforms.size() == 0) {
					throw new IllegalStateException(String.format(
							"No wordforms in Word %s in %s", word,
							getDocumentUri(jCas.getCas())));
				}
				Wordform tokWf = (Wordform) tokWordforms.get(0);
				String outputLabel = extractOutputLabel(tokWf);
				sentLabels.add(outputLabel);
			}
			List<Feature> tokFeatures = Lists.newLinkedList();
			tokFeatures.addAll(tokenFeatureExtractor.extract(jCas, word));
			tokFeatures.addAll(posExtractor.extract(jCas, word));
			tokFeatures.addAll(dictFeatureExtractor.extract(jCas, word));
			tokFeatures.addAll(contextFeatureExtractor.extract(jCas, word));
			sentSeq.add(tokFeatures);
		}
		if (isTraining()) {
			dataWriter.write(Instances.toInstances(sentLabels, sentSeq));
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private String extractOutputLabel(Wordform wf) {
		BitSet wfBits = toGramBits(morphDictionary, FSUtils.toList(wf.getGrammems()));
		wfBits.and(filterBS);
		if (wfBits.isEmpty()) {
			return null;
		}
		return targetGramJoiner.join(morphDictionary.toGramSet(wfBits));
	}

	private static final Joiner targetGramJoiner = Joiner.on('_');

	private void parsePosTiersParameter() {
		posTiers = Lists.newArrayList();
		for (String pPosTier : pPosTiers) {
			Set<String> posCats = ImmutableSet.copyOf(posCatSplitter.split(pPosTier));
			if (posCats.isEmpty()) {
				throw new IllegalStateException(String.format("Illegal posTiers parameter value"));
			}
			posTiers.add(posCats);
		}
		posTiers = ImmutableList.copyOf(posTiers);
		//
		prevTierPosCategories = Sets.newHashSet();
		for (int i = 0; i < currentTier; i++) {
			prevTierPosCategories.addAll(posTiers.get(i));
		}
		prevTierPosCategories = ImmutableSet.copyOf(prevTierPosCategories);
		currentPosTier = ImmutableSet.copyOf(posTiers.get(currentTier));
	}

	private void makeFilterBitSet() {
		this.filterBS = new BitSet();
		for (String posCat : currentPosTier) {
			BitSet posCatBits = morphDictionary.getGrammemWithChildrenBits(posCat, true);
			if (posCatBits == null) {
				throw new IllegalStateException(String.format(
						"Unknown grammeme (category): %s", posCat));
			}
			filterBS.or(posCatBits);
		}
	}

	private static final Splitter posCatSplitter = Splitter.on(',').trimResults();
	private static final SimpleFeatureExtractor[] FE_ARRAY = new SimpleFeatureExtractor[0];
}