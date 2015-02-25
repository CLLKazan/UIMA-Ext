package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.*;
import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.CombinedExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.uimafit.component.initialize.ExternalResourceInitializer;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.initializable.Initializable;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.ml.DictionaryPossibleTagFeatureExtractor;
import ru.kfu.itis.issst.uima.ml.GrammemeExtractor;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static ru.kfu.itis.issst.uima.ml.DefaultFeatureExtractors.contextTokenExtractors;
import static ru.kfu.itis.issst.uima.ml.DefaultFeatureExtractors.currentTokenExtractors;

/**
 * @author Rinat Gareev
 */
class PosTaggerFeatureExtractor implements IncrementalFeatureExtractor, Initializable {
    // constants
    public static final String RESOURCE_MORPH_DICTIONARY = "morphDictionary";
    public static final String RESOURCE_GRAM_MODEL = "gramModel";
    public static final String PARAM_LEFT_CONTEXT_SIZE = "leftContextSize";
    public static final String PARAM_RIGHT_CONTEXT_SIZE = "rightContextSize";

    // aggregate
    @ExternalResource(key = RESOURCE_MORPH_DICTIONARY, mandatory = true)
    private MorphDictionaryHolder morphDictHolder;
    @ExternalResource(key = RESOURCE_GRAM_MODEL, mandatory = true)
    private GramModelHolder gramModelHolder;
    @ConfigurationParameter(name = PARAM_LEFT_CONTEXT_SIZE, defaultValue = "2")
    private Integer leftContextSize;
    @ConfigurationParameter(name = PARAM_RIGHT_CONTEXT_SIZE, defaultValue = "2")
    private Integer rightContextSize;
    // TODO
    private GramTiers gramTiers;

    private FeatureExtractionPlan fePlan;
    private MorphDictionary morphDictionary;
    private GramModel gramModel;

    PosTaggerFeatureExtractor() {

    }

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        ExternalResourceInitializer.initialize(context, this);
        morphDictionary = morphDictHolder.getDictionary();
        gramModel = gramModelHolder.getGramModel();
        // parse context definitions for feature extractors
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
                // TODO:LOW introduce difference between Null and NotApplicable values
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
    public void extractNext(JCas view, Annotation contextSpan, Annotation focusAnnotation, FeatureSet featSet) {
        // TODO
        throw new UnsupportedOperationException();
    }



    private static final SimpleFeatureExtractor[] FE_ARRAY = new SimpleFeatureExtractor[0];

    private SortedSet<Integer> closedOpenRange(int first, int last) {
        return ContiguousSet.create(Range.closedOpen(first, last), DiscreteDomain.integers());
    }
}
