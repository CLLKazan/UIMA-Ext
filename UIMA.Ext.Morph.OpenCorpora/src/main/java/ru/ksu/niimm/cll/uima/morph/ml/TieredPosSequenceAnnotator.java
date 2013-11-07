/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.Instances;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
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
import ru.kfu.itis.issst.cleartk.Disposable;
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
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
	@ExternalResource(key = RESOURCE_KEY_MORPH_DICTIONARY, mandatory = true)
	private MorphDictionaryHolder morphDictHolder;
	@ConfigurationParameter(name = PARAM_POS_TIERS, mandatory = true)
	private List<String> pPosTiers;
	@ConfigurationParameter(name = PARAM_CURRENT_TIER, mandatory = true)
	private int currentTier = -1;
	// derived
	private MorphDictionary morphDictionary;
	private Set<String> currentPosTier;
	// TODO make bit masks immutable
	private BitSet currentTierMask;
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
		this.currentTierMask = makeBitMask(currentPosTier);
		// check grammems
		checkDictGrammems();

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
				currentPosTier, prevTierPosCategories, morphDictionary);

		contextFeatureExtractor = new CleartkExtractor(Word.class,
				new CombinedExtractor(contextFeatureExtractors.toArray(FE_ARRAY)),
				new CleartkExtractor.Preceding(2), new CleartkExtractor.Following(2));
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		if (!isTraining() && currentTier == 0) {
			// make Word annotations
			WordAnnotator.makeWords(jCas);
		}
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			process(jCas, sent);
		}
	}

	@Override
	public void destroy() {
		if (classifier instanceof Disposable) {
			((Disposable) classifier).dispose();
		}
		super.destroy();
	}

	private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		if (isTraining()) {
			trainingProcess(jCas, sent);
		} else {
			taggingProcess(jCas, sent);
		}
	}

	private void trainingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
		List<List<Feature>> sentSeq = Lists.newArrayList();
		List<String> sentLabels = Lists.newArrayList();
		for (Word word : JCasUtil.selectCovered(jCas, Word.class, sent)) {
			// TRAINING
			FSArray tokWordforms = word.getWordforms();
			if (tokWordforms == null || tokWordforms.size() == 0) {
				throw new IllegalStateException(String.format(
						"No wordforms in Word %s in %s", word,
						getDocumentUri(jCas.getCas())));
			}
			Wordform tokWf = (Wordform) tokWordforms.get(0);
			String outputLabel = extractOutputLabel(tokWf);
			sentLabels.add(outputLabel);
			List<Feature> tokFeatures = extractFeatures(jCas, word);
			sentSeq.add(tokFeatures);
		}
		// TRAINING
		dataWriter.write(Instances.toInstances(sentLabels, sentSeq));
	}

	private void taggingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
		List<List<Feature>> sentSeq = Lists.newArrayList();
		List<Wordform> wfSeq = Lists.newArrayList();
		for (Word word : JCasUtil.selectCovered(jCas, Word.class, sent)) {
			// TRAINING
			FSArray tokWordforms = word.getWordforms();
			if (tokWordforms == null || tokWordforms.size() == 0) {
				throw new IllegalStateException(String.format(
						"No wordforms in Word %s in %s", word,
						getDocumentUri(jCas.getCas())));
			}
			Wordform tokWf = (Wordform) tokWordforms.get(0);
			if (tokWf == null) {
				throw new NullPointerException("Token->Wordform");
			}
			wfSeq.add(tokWf);
			List<Feature> tokFeatures = extractFeatures(jCas, word);
			sentSeq.add(tokFeatures);
		}
		List<String> labelSeq = classifier.classify(sentSeq);
		if (labelSeq.size() != wfSeq.size()) {
			throw new IllegalStateException();
		}
		if (!(labelSeq instanceof RandomAccess)) {
			labelSeq = new ArrayList<String>(labelSeq);
		}
		for (int i = 0; i < labelSeq.size(); i++) {
			String label = labelSeq.get(i);
			if (label == null || label.isEmpty() || label.equalsIgnoreCase("null")) {
				// do nothing, it means there is no a new PoS-tag for this wordform
				continue;
			}
			Iterable<String> newGrams = targetGramSplitter.split(label);
			Wordform wf = wfSeq.get(i);
			MorphCasUtils.addGrammemes(jCas, wf, newGrams);
		}
	}

	private List<Feature> extractFeatures(JCas jCas, Word word) throws CleartkExtractorException {
		List<Feature> tokFeatures = Lists.newLinkedList();
		tokFeatures.addAll(tokenFeatureExtractor.extract(jCas, word));
		tokFeatures.addAll(posExtractor.extract(jCas, word));
		tokFeatures.addAll(dictFeatureExtractor.extract(jCas, word));
		tokFeatures.addAll(contextFeatureExtractor.extract(jCas, word));
		return tokFeatures;
	}

	private String extractOutputLabel(Wordform wf) {
		BitSet wfBits = toGramBits(morphDictionary, FSUtils.toList(wf.getGrammems()));
		wfBits.and(currentTierMask);
		if (wfBits.isEmpty()) {
			return null;
		}
		return targetGramJoiner.join(morphDictionary.toGramSet(wfBits));
	}

	private static final String targetGramDelim = "&";
	private static final Joiner targetGramJoiner = Joiner.on(targetGramDelim);
	private static final Splitter targetGramSplitter = Splitter.on(targetGramDelim);

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

	/**
	 * @param posCats
	 * @return bit mask for all PoS-categories in argument posCats
	 */
	private BitSet makeBitMask(Iterable<String> posCats) {
		BitSet result = new BitSet();
		for (String posCat : posCats) {
			BitSet posCatBits = morphDictionary.getGrammemWithChildrenBits(posCat, true);
			if (posCatBits == null) {
				throw new IllegalStateException(String.format(
						"Unknown grammeme (category): %s", posCat));
			}
			result.or(posCatBits);
		}
		return result;
	}

	private void checkDictGrammems() {
		for (int grId = 0; grId < morphDictionary.getGrammemMaxNumId(); grId++) {
			Grammeme gr = morphDictionary.getGrammem(grId);
			if (gr != null && gr.getId().contains(targetGramDelim)) {
				throw new IllegalStateException(String.format(
						"Grammeme %s contains character that is used as delimiter in this class",
						gr.getId()));
			}
		}
	}

	private static final Splitter posCatSplitter = Splitter.on(',').trimResults();
	private static final SimpleFeatureExtractor[] FE_ARRAY = new SimpleFeatureExtractor[0];
}