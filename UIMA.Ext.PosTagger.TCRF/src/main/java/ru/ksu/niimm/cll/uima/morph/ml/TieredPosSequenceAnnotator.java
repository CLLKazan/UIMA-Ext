/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryUtils.toGramBits;
import static ru.ksu.niimm.cll.uima.morph.ml.DefaultFeatureExtractors.contextTokenExtractors;
import static ru.ksu.niimm.cll.uima.morph.ml.DefaultFeatureExtractors.currentTokenExtractors;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.Instances;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Context;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.CombinedExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.cleartk.Disposable;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;
import ru.kfu.itis.issst.uima.morph.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils;

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
	public static final String PARAM_LEFT_CONTEXT_SIZE = "leftContextSize";
	public static final String PARAM_RIGHT_CONTEXT_SIZE = "rightContextSize";
	public static final String PARAM_GEN_DICTIONARY_FEATURES = "generateDictionaryFeatures";
	public static final String PARAM_GEN_PUNCTUATION_FEATURES = "generatePunctuationFeatures";
	public static final String PARAM_REUSE_EXISTING_WORD_ANNOTATIONS = "reuseExistingWordAnnotations";
	// config fields
	@ExternalResource(key = RESOURCE_KEY_MORPH_DICTIONARY, mandatory = true)
	private MorphDictionaryHolder morphDictHolder;
	@ConfigurationParameter(name = PARAM_POS_TIERS, mandatory = true)
	private List<String> pPosTiers;
	@ConfigurationParameter(name = PARAM_CURRENT_TIER, mandatory = true)
	private int currentTier = -1;
	// feature extraction parameters
	@ConfigurationParameter(name = PARAM_LEFT_CONTEXT_SIZE, defaultValue = "2")
	private int leftContextSize = -1;
	@ConfigurationParameter(name = PARAM_RIGHT_CONTEXT_SIZE, defaultValue = "2")
	private int rightContextSize = -1;
	@ConfigurationParameter(name = PARAM_GEN_DICTIONARY_FEATURES, defaultValue = "true")
	private boolean generateDictionaryFeatures;
	@ConfigurationParameter(name = PARAM_GEN_PUNCTUATION_FEATURES, defaultValue = "false")
	private boolean generatePunctuationFeatures;
	@ConfigurationParameter(name = PARAM_REUSE_EXISTING_WORD_ANNOTATIONS, defaultValue = "false")
	private boolean reuseExistingWordAnnotations;
	// derived
	private MorphDictionary morphDictionary;
	private GramModel gramModel;
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
	// per-CAS
	private SimpleFeatureExtractor adjacentPunctuationFeatureExtractor;
	//
	private Map<Token, Word> token2WordIndex;

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
		gramModel = morphDictionary.getGramModel();
		this.currentTierMask = makeBitMask(currentPosTier);
		// check grammems
		checkDictGrammems();

		tokenFeatureExtractor = new CombinedExtractor(currentTokenExtractors().toArray(
				new SimpleFeatureExtractor[0]));

		List<SimpleFeatureExtractor> gramExtractors = Lists.newArrayList();
		List<SimpleFeatureExtractor> contextFeatureExtractors = contextTokenExtractors();
		for (String posCat : prevTierPosCategories) {
			GrammemeExtractor gramExtractor = new GrammemeExtractor(gramModel, posCat);
			gramExtractors.add(gramExtractor);
			contextFeatureExtractors.add(gramExtractor);
		}
		// TODO introduce difference between Null and NotApplicable values
		posExtractor = new CombinedExtractor(gramExtractors.toArray(FE_ARRAY));
		if (generateDictionaryFeatures) {
			dictFeatureExtractor = new DictionaryPossibleTagFeatureExtractor(
					currentPosTier, prevTierPosCategories, morphDictionary);
		}

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
		contextFeatureExtractor = new CleartkExtractor(Token.class,
				new CombinedExtractor(contextFeatureExtractors.toArray(FE_ARRAY)),
				contexts.toArray(new Context[contexts.size()]));
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		if (!isTraining() && currentTier == 0) {
			if (reuseExistingWordAnnotations) {
				// clean wordforms to avoid erroneous feature extraction or output assignment
				cleanWordforms(jCas);
			} else {
				// ensure that there are no existing annotations
				// // otherwise things may go irregularly
				if (JCasUtil.exists(jCas, Word.class)) {
					throw new IllegalStateException(String.format(
							"CAS '%s' has Word annotations before this annotator",
							getDocumentUri(jCas)));
				}
				// make Word annotations
				WordAnnotator.makeWords(jCas);
			}
		}
		token2WordIndex = MorphCasUtils.getToken2WordIndex(jCas);
		try {
			if (generatePunctuationFeatures) {
				// adjacentPunctuationFeatureExtractor = new AdjacentPunctuationFeatureExtractor(jCas);
				throw new UnsupportedOperationException("generatePunctuationFeatures == true");
			}
			for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
				process(jCas, sent);
			}
		} finally {
			adjacentPunctuationFeatureExtractor = null;
			token2WordIndex.clear();
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
		for (Token token : JCasUtil.selectCovered(jCas, Token.class, sent)) {
			// classification label
			String outputLabel;
			Word word = token2WordIndex.get(token);
			if (word == null) {
				if (token instanceof NUM || token instanceof W) {
					throw new IllegalStateException(String.format(
							"Token %s in %s does not have corresponding Word annotation",
							toPrettyString(token), getDocumentUri(jCas)));
				}
				outputLabel = PunctuationUtils.OTHER_PUNCTUATION_TAG;
			} else {
				Wordform wf = MorphCasUtils.requireOnlyWordform(word);
				outputLabel = extractOutputLabel(wf);
			}
			sentLabels.add(outputLabel);
			List<Feature> tokFeatures = extractFeatures(jCas, token);
			sentSeq.add(tokFeatures);
		}
		dataWriter.write(Instances.toInstances(sentLabels, sentSeq));
	}

	private void taggingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
		List<List<Feature>> sentSeq = Lists.newArrayList();
		List<Wordform> wfSeq = Lists.newArrayList();
		for (Token token : JCasUtil.selectCovered(jCas, Token.class, sent)) {
			Word word = token2WordIndex.get(token);
			if (word == null) {
				wfSeq.add(null);
			} else {
				Wordform tokWf = MorphCasUtils.requireOnlyWordform(word);
				wfSeq.add(tokWf);
			}
			List<Feature> tokFeatures = extractFeatures(jCas, token);
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
			Wordform wf = wfSeq.get(i);
			if (wf == null) {
				if (!label.equals(PunctuationUtils.OTHER_PUNCTUATION_TAG)) {
					getLogger().warn(String.format(
							"Classifier predicted the gram value for a non-word token: %s",
							label));
				}
				// else - punctuation tag for punctuation token - OK
			} else if (label.equals(PunctuationUtils.OTHER_PUNCTUATION_TAG)) {
				getLogger().warn("Classifier predicted the punctuation tag for a word token");
			} else {
				Iterable<String> newGrams = targetGramSplitter.split(label);
				MorphCasUtils.addGrammemes(jCas, wf, newGrams);
			}
		}
	}

	private List<Feature> extractFeatures(JCas jCas, Token token) throws CleartkExtractorException {
		List<Feature> tokFeatures = Lists.newLinkedList();
		tokFeatures.addAll(tokenFeatureExtractor.extract(jCas, token));
		tokFeatures.addAll(posExtractor.extract(jCas, token));
		if (dictFeatureExtractor != null) {
			tokFeatures.addAll(dictFeatureExtractor.extract(jCas, token));
		}
		tokFeatures.addAll(contextFeatureExtractor.extract(jCas, token));
		if (generatePunctuationFeatures) {
			tokFeatures.addAll(adjacentPunctuationFeatureExtractor.extract(jCas, token));
		}
		return tokFeatures;
	}

	private String extractOutputLabel(Wordform wf) {
		BitSet wfBits = toGramBits(gramModel, FSUtils.toList(wf.getGrammems()));
		wfBits.and(currentTierMask);
		if (wfBits.isEmpty()) {
			return null;
		}
		return targetGramJoiner.join(gramModel.toGramSet(wfBits));
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
			BitSet posCatBits = gramModel.getGrammemWithChildrenBits(posCat, true);
			if (posCatBits == null) {
				throw new IllegalStateException(String.format(
						"Unknown grammeme (category): %s", posCat));
			}
			result.or(posCatBits);
		}
		return result;
	}

	private void checkDictGrammems() {
		for (int grId = 0; grId < gramModel.getGrammemMaxNumId(); grId++) {
			Grammeme gr = gramModel.getGrammem(grId);
			if (gr != null && gr.getId().contains(targetGramDelim)) {
				throw new IllegalStateException(String.format(
						"Grammeme %s contains character that is used as delimiter in this class",
						gr.getId()));
			}
		}
	}

	static final Splitter posCatSplitter = Splitter.on('&').trimResults();
	private static final SimpleFeatureExtractor[] FE_ARRAY = new SimpleFeatureExtractor[0];

	private void cleanWordforms(JCas jCas) {
		for (Word w : JCasUtil.select(jCas, Word.class)) {
			Wordform wf = new Wordform(jCas);
			wf.setWord(w);
			w.setWordforms(FSUtils.toFSArray(jCas, wf));
		}
	}
}