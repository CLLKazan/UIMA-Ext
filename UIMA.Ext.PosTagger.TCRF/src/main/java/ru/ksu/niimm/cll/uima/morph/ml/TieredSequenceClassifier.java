package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.Feature;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.lang.String.format;

/**
 * A template for composite tiered sequence classifier implementation.
 * Subclasses have to:
 * <ul>
 * <li>initialize underlying classifiers for each tier,</li>
 * <li>implement a feature extraction.</li>
 * </ul>
 *
 * @author Rinat Gareev
 */
public abstract class TieredSequenceClassifier implements SequenceClassifier<String> {

    protected List<org.cleartk.classifier.SequenceClassifier<String>> classifiers;

    @Override
    public List<String> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq)
            throws CleartkProcessingException {
        // create a feature set for each token
        List<FeatureSet> featSets = newArrayListWithCapacity(seq.size());
        List<StringBuilder> resultLabels = newArrayListWithCapacity(seq.size());
        for (FeatureStructure _tok : seq) {
            Token tok = (Token) _tok;
            featSets.add(extractCommonFeatures(jCas, spanAnno, tok));
            resultLabels.add(new StringBuilder(DEFAULT_TAG_BUILDER_CAPACITY));
        }
        //
        for (int tier = 0; tier < classifiers.size(); tier++) {
            for (int tokIdx = 0; tokIdx < seq.size(); tokIdx++) {
                Token tok = (Token) seq.get(tokIdx);
                FeatureSet tokFeatSet = featSets.get(tokIdx);
                if (tier > 0) {
                    int prevTier = tier - 1;
                    deleteTierSpecificFeatures(tokFeatSet, prevTier);
                }
                addTierSpecificFeatures(tokFeatSet, tier, jCas, spanAnno, tok);
            }
            // invoke a classifier of the current tier
            List<List<Feature>> featValues = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<String> labelSeq = getClassifier(tier).classify(featValues);
            if (labelSeq.size() != resultLabels.size())
                throw new IllegalStateException(format(
                        "Expected outcomes: %s, actual: %s", resultLabels.size(), labelSeq.size()));
            Iterator<String> tierLabelIter = labelSeq.iterator();
            Iterator<StringBuilder> resultIter = resultLabels.iterator();
            while (tierLabelIter.hasNext()) {
                String tierLabel = tierLabelIter.next();
                StringBuilder rb = resultIter.next();
                if (!PUtils.isNullLabel(tierLabel)) {
                    if (rb.length() != 0) {
                        rb.append('&');
                    }
                    rb.append(tierLabel);
                }
            }
        }
        return new ArrayList<String>(Lists.transform(resultLabels, new Function<StringBuilder, String>() {
            @Override
            public String apply(StringBuilder input) {
                return input.toString();
            }
        }));
    }

    protected abstract void addTierSpecificFeatures(FeatureSet tokFeatSet, int tier,
                                                    JCas jCas, Annotation spanAnno, Token tok);

    protected abstract void deleteTierSpecificFeatures(FeatureSet tokFeatSet, int tier);

    protected abstract FeatureSet extractCommonFeatures(JCas jCas, Annotation spanAnno, Token tok);

    private org.cleartk.classifier.SequenceClassifier<String> getClassifier(int tier) {
        return classifiers.get(tier);
    }

    private static final int DEFAULT_TAG_BUILDER_CAPACITY = 32;
}
