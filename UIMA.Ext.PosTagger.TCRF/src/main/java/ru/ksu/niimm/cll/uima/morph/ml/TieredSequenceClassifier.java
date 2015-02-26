package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.Feature;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import java.io.Closeable;
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
 * <li>initialize a feature extractor.</li>
 * </ul>
 *
 * @author Rinat Gareev
 */
public abstract class TieredSequenceClassifier implements SequenceClassifier<String> {

    protected List<org.cleartk.classifier.SequenceClassifier<String>> classifiers;
    protected TieredFeatureExtractor featureExtractor;

    @Override
    public List<String> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq)
            throws CleartkProcessingException {
        @SuppressWarnings("unchecked") List<Token> tokens = (List<Token>) seq;
        // create a feature set for each token
        List<FeatureSet> featSets = featureExtractor.extractCommonFeatures(jCas, spanAnno, tokens);
        List<StringBuilder> resultLabels = newArrayListWithCapacity(tokens.size());
        for (Token tok : tokens) {
            resultLabels.add(new StringBuilder(DEFAULT_TAG_BUILDER_CAPACITY));
        }
        //
        for (int tier = 0; tier < classifiers.size(); tier++) {
            featureExtractor.onBeforeTier(featSets, tier, jCas, spanAnno, tokens);
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
            // if not the last tier
            if (tier != classifiers.size() - 1) {
                featureExtractor.onAfterTier(featSets, labelSeq, tier, jCas, spanAnno, tokens);
            }
        }
        return new ArrayList<String>(Lists.transform(resultLabels, new Function<StringBuilder, String>() {
            @Override
            public String apply(StringBuilder input) {
                return input.toString();
            }
        }));
    }

    @Override
    public void close() {
        for (org.cleartk.classifier.SequenceClassifier<String> cl : classifiers)
            if (cl instanceof Closeable) {
                IOUtils.closeQuietly((Closeable) cl);
            }
    }

    private org.cleartk.classifier.SequenceClassifier<String> getClassifier(int tier) {
        return classifiers.get(tier);
    }

    private static final int DEFAULT_TAG_BUILDER_CAPACITY = 32;
}
