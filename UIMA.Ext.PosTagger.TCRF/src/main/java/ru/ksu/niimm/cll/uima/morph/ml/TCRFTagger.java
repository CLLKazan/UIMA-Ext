/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.*;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Context;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.CombinedExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.cleartk.Disposable;
import ru.kfu.itis.issst.uima.ml.DictionaryPossibleTagFeatureExtractor;
import ru.kfu.itis.issst.uima.ml.GrammemeExtractor;
import ru.kfu.itis.issst.uima.ml.WordAnnotator;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;
import ru.kfu.itis.issst.uima.morph.model.Grammeme;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import java.util.*;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.ml.DefaultFeatureExtractors.contextTokenExtractors;
import static ru.kfu.itis.issst.uima.ml.DefaultFeatureExtractors.currentTokenExtractors;
import static ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryUtils.toGramBits;
import static ru.kfu.itis.issst.uima.postagger.PosTaggerAPI.DEFAULT_REUSE_EXISTING_WORD_ANNOTATIONS;
import static ru.kfu.itis.issst.uima.postagger.PosTaggerAPI.PARAM_REUSE_EXISTING_WORD_ANNOTATIONS;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class TCRFTagger extends JCasAnnotator_ImplBase {

    public static final String RESOURCE_MORPH_DICTIONARY = "morphDictionary";
    public static final String PARAM_TIERS = "tiers";
    public static final String PARAM_LEFT_CONTEXT_SIZE = "leftContextSize";
    public static final String PARAM_RIGHT_CONTEXT_SIZE = "rightContextSize";
    // config fields
    @ExternalResource(key = RESOURCE_MORPH_DICTIONARY, mandatory = true)
    private MorphDictionaryHolder morphDictHolder;
    @ConfigurationParameter(name = PARAM_TIERS, mandatory = true)
    private List<String> tierDefs;
    // feature extraction parameters
    @ConfigurationParameter(name = PARAM_LEFT_CONTEXT_SIZE, defaultValue = "2")
    private int leftContextSize = -1;
    @ConfigurationParameter(name = PARAM_RIGHT_CONTEXT_SIZE, defaultValue = "2")
    private int rightContextSize = -1;
    @ConfigurationParameter(name = PARAM_REUSE_EXISTING_WORD_ANNOTATIONS,
            defaultValue = DEFAULT_REUSE_EXISTING_WORD_ANNOTATIONS)
    private boolean reuseExistingWordAnnotations;
    // derived
    private MorphDictionary morphDictionary;
    private GramModel gramModel;
    private GramTiers gramTiers;
    private FeatureExtractionPlan fePlan;
    //
    private Map<Token, Word> token2WordIndex;

    @Override
    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        // validate tiers configuration
        gramTiers = parseGramTiers(tierDefs);
        morphDictionary = morphDictHolder.getDictionary();
        gramModel = morphDictionary.getGramModel();
        // check grammems
        checkDictGrammems();
        // parse context definitions for feature extractors
        // TODO:LOW here should be a single feature extraction config like in the Stanford tagger
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
        Context[] contextsArr = contexts.toArray(new Context[contexts.size()]);
        // build a feature extraction plan
        FeatureExtractionPlanBuilder planBuilder = new FeatureExtractionPlanBuilder();
        // CFE ~ common (shared) feature extractor
        SimpleFeatureExtractor tokenFE = new CombinedExtractor(currentTokenExtractors().toArray(FE_ARRAY));
        planBuilder.addExtractors(closedOpenRange(0, gramTiers.getCount()), tokenFE);
        // context CFE
        CleartkExtractor contextCFE = new CleartkExtractor(Token.class,
                new CombinedExtractor(contextTokenExtractors().toArray(FE_ARRAY)),
                contextsArr);
        planBuilder.addExtractors(closedOpenRange(0, gramTiers.getCount()), contextCFE);
        //
        for (int step = 1; step < gramTiers.getCount(); step++) {
            // add grammeme extractors for categories that have been extracted in the previous step
            Set<String> prevStepCats = gramTiers.getTierCategories(step - 1);
            // range from ths step till the end
            SortedSet<Integer> targetSteps = ContiguousSet.create(
                    Range.closedOpen(step, gramTiers.getCount()), DiscreteDomain.integers());
            for (String gramCat : prevStepCats) {
                // TODO introduce difference between Null and NotApplicable values
                GrammemeExtractor gramExtractor = new GrammemeExtractor(gramModel, gramCat);
                CleartkExtractor ctxGramExtractor = new CleartkExtractor(
                        Token.class, gramExtractor, contextsArr);
                planBuilder.addExtractors(targetSteps, gramExtractor);
                planBuilder.addExtractors(targetSteps, ctxGramExtractor);
            }
        }
        //
        for (int step = 0; step < gramTiers.getCount(); step++) {
            Set<String> curTierCats = gramTiers.getTierCategories(step);
            Set<String> prevTierCats = Sets.newHashSet();
            for (int prevStep = 0; prevStep < step; prevStep++) {
                prevTierCats.addAll(gramTiers.getTierCategories(prevStep));
            }
            SimpleFeatureExtractor dictFeatureExtractor = new DictionaryPossibleTagFeatureExtractor(
                    curTierCats, prevTierCats, morphDictionary);
            planBuilder.addExtractors(step, dictFeatureExtractor);
        }
        fePlan = planBuilder.build();
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        if (!isTraining()) {
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
            for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
                process(jCas, sent);
            }
        } finally {
            token2WordIndex.clear();
        }
    }

    private boolean isTraining() {
        // TODO
        throw new UnsupportedOperationException();
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
        // extract sentence tokens
        List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sent);
        if (tokens.isEmpty()) return;
        // extract sentence wordforms
        List<Wordform> wfSeq = newArrayListWithCapacity(tokens.size());
        for (Token token : tokens) {
            Word word = token2WordIndex.get(token);
            if (word == null) {
                wfSeq.add(null);
            } else {
                Wordform tokWf = MorphCasUtils.requireOnlyWordform(word);
                wfSeq.add(tokWf);
            }
        }
        //
        List<FeatureSet> featSets = newArrayListWithCapacity(tokens.size());
        for (Token tok : tokens) {
            featSets.add(FeatureSets.empty());
        }
        //
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            List<String> outLabels = newArrayListWithCapacity(tokens.size());
            // prepare feature values and extract output labels
            for (int tokIdx = 0; tokIdx < tokens.size(); tokIdx++) {
                Token tok = tokens.get(tokIdx);
                FeatureSet tokFeatSet = featSets.get(tokIdx);
                extractFeatures(tier, tokFeatSet, jCas, sent, tok);
                outLabels.add(extractOutputLabel(tier, jCas, tok));
            }
            // write as training data
            List<List<Feature>> featValues = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<Instance<String>> instances = Instances.toInstances(outLabels, featValues);
            getTrainingDataWriter(tier).write(instances);
        }
    }

    private void taggingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
        // extract sentence tokens
        List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sent);
        if (tokens.isEmpty()) return;
        // extract sentence wordforms
        List<Wordform> wfSeq = newArrayListWithCapacity(tokens.size());
        for (Token token : tokens) {
            Word word = token2WordIndex.get(token);
            if (word == null) {
                wfSeq.add(null);
            } else {
                Wordform tokWf = MorphCasUtils.requireOnlyWordform(word);
                wfSeq.add(tokWf);
            }
        }
        //
        List<FeatureSet> featSets = newArrayListWithCapacity(tokens.size());
        for (Token tok : tokens) {
            featSets.add(FeatureSets.empty());
        }
        //
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            // prepare feature values
            for (int tokIdx = 0; tokIdx < tokens.size(); tokIdx++) {
                Token tok = tokens.get(tokIdx);
                FeatureSet tokFeatSet = featSets.get(tokIdx);
                extractFeatures(tier, tokFeatSet, jCas, sent, tok);
            }
            // invoke a classifier of the current tier
            List<List<Feature>> featValues = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<String> labelSeq = getClassifier(tier).classify(featValues);
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
    }

    private void extractFeatures(int tier, FeatureSet feats, JCas jCas, Sentence sent, Token token)
            throws CleartkExtractorException {
        // delete feature values that have been extracted for prev tiers but not required for the current
        feats.removeFeaturesBySource(fePlan.getExtractorsToRemoveOn(tier));
        // extract new feature values
        for (SimpleFeatureExtractor fe : fePlan.getNewFeatureExtractors(tier)) {
            List<Feature> newVals;
            if (fe instanceof CleartkExtractor) {
                newVals = ((CleartkExtractor) fe).extractWithin(jCas, token, sent);
            } else {
                newVals = fe.extract(jCas, token);
            }
            feats.add(newVals, fe);
        }
    }

    private String extractOutputLabel(int tier, JCas jCas, Token token) {
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
            outputLabel = extractOutputLabel(tier, wf);
        }
        return outputLabel;
    }

    private String extractOutputLabel(int tier, Wordform wf) {
        BitSet wfBits = toGramBits(gramModel, FSUtils.toList(wf.getGrammems()));
        wfBits.and(gramTiers.getTierMask(tier));
        if (wfBits.isEmpty()) {
            return null;
        }
        return targetGramJoiner.join(gramModel.toGramSet(wfBits));
    }

    private static final String targetGramDelim = "&";
    private static final Joiner targetGramJoiner = Joiner.on(targetGramDelim);
    private static final Splitter targetGramSplitter = Splitter.on(targetGramDelim);

    private SequenceClassifier<String> getClassifier(int tier) {
        // TODO
        throw new UnsupportedOperationException();
    }

    private SequenceDataWriter<String> getTrainingDataWriter(int tier) {
        // TODO
        throw new UnsupportedOperationException();
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

    private static GramTiers parseGramTiers(List<String> paramValList) {
        List<Set<String>> tierCatsList = Lists.newArrayList();
        for (String tierDef : paramValList) {
            Set<String> tierCats = ImmutableSet.copyOf(posCatSplitter.split(tierDef));
            if (tierCats.isEmpty()) {
                throw new IllegalStateException(String.format("Illegal posTiers parameter value"));
            }
            tierCatsList.add(tierCats);
        }
        final List<Set<String>> finalTierCatsList = ImmutableList.copyOf(tierCatsList);
        return new GramTiers() {
            @Override
            public int getCount() {
                return finalTierCatsList.size();
            }

            @Override
            public Set<String> getTierCategories(int i) {
                return finalTierCatsList.get(i);
            }
        };
    }

    private SortedSet<Integer> closedOpenRange(int first, int last) {
        return ContiguousSet.create(Range.closedOpen(first, last), DiscreteDomain.integers());
    }

    private interface GramTiers {
        int getCount();

        Set<String> getTierCategories(int i);

        BitSet getTierMask(int i);
    }

    private interface FeatureExtractionPlan {
        Set<SimpleFeatureExtractor> getExtractorsToRemoveOn(int step);

        Set<SimpleFeatureExtractor> getNewFeatureExtractors(int step);
    }

    private class FeatureExtractionPlanBuilder {
        private SetMultimap<Integer, SimpleFeatureExtractor> extractionAgenda
                = LinkedHashMultimap.create();

        void addExtractors(Set<Integer> steps, SimpleFeatureExtractor fe) {
            for (Integer s : steps) {
                addExtractors(s, fe);
            }
        }

        void addExtractors(int step, SimpleFeatureExtractor fe) {
            extractionAgenda.put(step, fe);
        }

        public FeatureExtractionPlan build() {
            final List<Set<SimpleFeatureExtractor>> stepExtractorsList = Lists.newArrayList();
            final List<Set<SimpleFeatureExtractor>> stepRemovalsList = Lists.newArrayList();
            //
            final int maxStep = Collections.max(extractionAgenda.keys());
            Set<SimpleFeatureExtractor> prevStepFEs = ImmutableSet.of();
            //
            for (int step = 0; step < maxStep; step++) {
                Set<SimpleFeatureExtractor> stepFEs = ImmutableSet.copyOf(extractionAgenda.get(step));
                stepExtractorsList.add(Sets.difference(stepFEs, prevStepFEs));
                stepRemovalsList.add(Sets.difference(prevStepFEs, stepFEs));
                prevStepFEs = stepFEs;
            }
            //
            return new FeatureExtractionPlan() {
                @Override
                public Set<SimpleFeatureExtractor> getExtractorsToRemoveOn(int step) {
                    validateStep(step);
                    return stepRemovalsList.get(step);
                }

                @Override
                public Set<SimpleFeatureExtractor> getNewFeatureExtractors(int step) {
                    validateStep(step);
                    return stepExtractorsList.get(step);
                }

                private void validateStep(int step) {
                    if (step < 0 || step > maxStep)
                        throw new IllegalArgumentException("Too big step: " + step);
                }
            };
        }
    }
}
