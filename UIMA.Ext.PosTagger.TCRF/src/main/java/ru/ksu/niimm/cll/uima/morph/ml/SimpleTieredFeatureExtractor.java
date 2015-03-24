package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.CleartkExtractor;
import org.cleartk.ml.feature.extractor.CleartkExtractorException;
import org.cleartk.ml.feature.extractor.CombinedExtractor1;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.util.CacheKey;
import ru.kfu.itis.cll.uima.util.CachedResourceTuple;
import ru.kfu.itis.issst.uima.ml.*;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static com.google.common.collect.Lists.transform;
import static ru.kfu.itis.cll.uima.util.ConfigPropertiesUtils.getIntProperty;
import static ru.kfu.itis.issst.uima.ml.DefaultFeatureExtractors.contextTokenExtractors;
import static ru.kfu.itis.issst.uima.ml.DefaultFeatureExtractors.currentTokenExtractors;
import static ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory.getMorphDictionaryAPI;

/**
 * @author Rinat Gareev
 */
public class SimpleTieredFeatureExtractor implements TieredFeatureExtractor<Token, String> {

    // constants
    public static final String CFG_LEFT_CONTEXT_SIZE = "leftContextSize";
    public static final String CFG_RIGHT_CONTEXT_SIZE = "rightContextSize";

    private Integer leftContextSize;
    private Integer rightContextSize;
    private List<String> gramTierDefs;
    private GramTiers gramTiers;
    // aggregate fields
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private CacheKey morphDictionaryKey;
    private MorphDictionary morphDictionary;
    // CFE ~ a Common Feature Extractor
    private FeatureExtractor1 tokenCFE;
    private CleartkExtractor contextCFE;
    private List<DictionaryPossibleTagFeatureExtractor> dictFeatureExtractors;

    public SimpleTieredFeatureExtractor() {
    }

    @Override
    public void initialize(Properties props) throws ResourceInitializationException {
        leftContextSize = getIntProperty(props, CFG_LEFT_CONTEXT_SIZE);
        rightContextSize = getIntProperty(props, CFG_RIGHT_CONTEXT_SIZE);
        gramTierDefs = TieredFeatureExtractors.getTiers(props);
        // TODO use a dependency injection
        try {
            CachedResourceTuple<MorphDictionary> t = getMorphDictionaryAPI().getCachedInstance();
            morphDictionary = t.getResource();
            morphDictionaryKey = t.getCacheKey();
        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
        initialize();
    }

    private void initialize() {
        gramTiers = GramTiersFactory.parseGramTiers(morphDictionary.getGramModel(), gramTierDefs);
        // TODO:LOW here should be a single feature extraction config like in the Stanford tagger
        if (leftContextSize < 0 || rightContextSize < 0) {
            throw new IllegalStateException("context size < 0");
        }
        if (leftContextSize == 0 && rightContextSize == 0) {
            throw new IllegalStateException("left & right context sizes == 0");
        }
        List<CleartkExtractor.Context> contexts = Lists.newArrayList();
        if (leftContextSize > 0) {
            contexts.add(new CleartkExtractor.Preceding(leftContextSize));
        }
        if (rightContextSize > 0) {
            contexts.add(new CleartkExtractor.Following(rightContextSize));
        }
        CleartkExtractor.Context[] contextsArr = contexts.toArray(new CleartkExtractor.Context[contexts.size()]);
        // instantiate feature extractors
        tokenCFE = new CombinedExtractor1(currentTokenExtractors());
        contextCFE = new CleartkExtractor(Token.class,
                new CombinedExtractor1(contextTokenExtractors()),
                contextsArr);
        //
        dictFeatureExtractors = Lists.newArrayList();
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            Set<String> curTierCats = gramTiers.getTierCategories(tier);
            DictionaryPossibleTagFeatureExtractor dfe = new DictionaryPossibleTagFeatureExtractor(
                    curTierCats, null, morphDictionary);
            dictFeatureExtractors.add(dfe);
        }
    }

    @Override
    public void onBeforeTier(List<FeatureSet> featSets, List<List<String>> labels, int tier,
                             JCas jCas, Annotation spanAnno, List<? extends Token> tokens)
            throws CleartkExtractorException {
        Preconditions.checkArgument(featSets.size() == labels.size());
        Preconditions.checkArgument(featSets.size() == tokens.size());
        //
        DictionaryPossibleTagFeatureExtractor dfe = dictFeatureExtractors.get(tier);
        for (int i = 0; i < featSets.size(); i++) {
            Token tok = tokens.get(i);
            List<String> tokLabel = labels.get(i);
            FeatureSet tokFeatSet = featSets.get(i);
            // TODO:LOW depends on logic somewhere before (in a containing annotator)
            if (WordAnnotator.canCarryWord(tok)) {
                List<Set<String>> tokGramsTiered = parseLabelIntoGrams(tokLabel);
                Set<String> tokGrams = merge(tokGramsTiered);
                tokFeatSet.add(dfe.extract(tok.getCoveredText(), tokGrams), dfe);
            }
        }
    }

    // TODO define in a single place (probably, a kind of 'Output Label Generator' in a training data writer)
    // Note that this is different from the tier joiner, even though currently they use the same separator char
    private static final Splitter GRAM_SPLITTER = Splitter.on('&');

    /**
     * @param labels list of labels for each token where each label is composite (tiered)
     * @return list of grammemes decomposition for each token
     */
    private List<List<Set<String>>> parseLabelsIntoGrams(List<List<String>> labels) {
        List<List<Set<String>>> result = newArrayListWithExpectedSize(labels.size());
        for (List<String> tokLabel : labels) {
            result.add(parseLabelIntoGrams(tokLabel));
        }
        return result;
    }

    private List<Set<String>> parseLabelIntoGrams(List<String> label) {
        List<Set<String>> result = newArrayListWithExpectedSize(label.size());
        for (String tierLabel : label)
            if (Strings.isNullOrEmpty(tierLabel)) result.add(ImmutableSet.<String>of());
            else result.add(ImmutableSet.copyOf(GRAM_SPLITTER.split(tierLabel)));
        return result;
    }

    private static <T> Set<T> merge(Iterable<? extends Set<T>> sets) {
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        for (Set<T> set : sets) {
            builder.addAll(set);
        }
        return builder.build();
    }

    @Override
    public void onAfterTier(List<FeatureSet> featSets, List<List<String>> labels, final int tier,
                            JCas jCas, Annotation spanAnno, List<? extends Token> tokens) {
        Preconditions.checkArgument(featSets.size() == labels.size());
        Preconditions.checkArgument(featSets.size() == tokens.size());
        // parse tier output labels into feature values
        List<List<Set<String>>> parsedLabels = parseLabelsIntoGrams(labels);
        List<Set<String>> curTierParsedLabels = transform(parsedLabels, new Function<List<Set<String>>, Set<String>>() {
            @Override
            public Set<String> apply(List<Set<String>> input) {
                return input.get(tier);
            }
        });
        //
        FeatureExtractor1 dfe = dictFeatureExtractors.get(tier);
        for (int tokPos = 0; tokPos < featSets.size(); tokPos++) {
            // remove tier-specific features
            FeatureSet tokFeatSet = featSets.get(tokPos);
            tokFeatSet.removeFeaturesBySource(dfe);
            //

            // extract feature from a new data - the new label of this tier
            List<Feature> gramFeatures = newArrayListWithExpectedSize(leftContextSize + rightContextSize + 1);
            int left = Math.max(0, tokPos - leftContextSize);
            int right = Math.min(tokens.size() - 1, tokPos + rightContextSize);
            for (int contextTokPos = left; contextTokPos <= right; contextTokPos++) {
                // a context token relative position
                final int contextTokRelPos = tokPos - contextTokPos;
                for (String gram : curTierParsedLabels.get(contextTokPos)) {
                    gramFeatures.add(new Feature("Gram_at_" + contextTokRelPos, gram));
                }
            }
            tokFeatSet.add(gramFeatures, mockGramExtractor);
        }
    }

    @Override
    public List<FeatureSet> extractCommonFeatures(JCas jCas, Annotation spanAnno, List<? extends Token> tokens)
            throws CleartkExtractorException {
        List<FeatureSet> resultList = newArrayListWithExpectedSize(tokens.size());
        for (Token tok : tokens) {
            FeatureSet fs = FeatureSets.empty();
            fs.add(tokenCFE.extract(jCas, tok), tokenCFE);
            fs.add(contextCFE.extractWithin(jCas, tok, spanAnno), contextCFE);
            resultList.add(fs);
        }
        return resultList;
    }

    public GramTiers getGramTiers() {
        return gramTiers;
    }

    private static final FeatureExtractor1[] FE_ARRAY = new FeatureExtractor1[0];

    private final FeatureExtractor1 mockGramExtractor = new FeatureExtractor1() {
        @Override
        public List<Feature> extract(JCas view, Annotation focusAnnotation) throws CleartkExtractorException {
            // should never be called
            throw new UnsupportedOperationException();
        }
    };
}
