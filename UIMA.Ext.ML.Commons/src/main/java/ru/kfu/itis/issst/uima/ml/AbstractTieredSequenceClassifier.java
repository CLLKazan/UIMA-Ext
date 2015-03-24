package ru.kfu.itis.issst.uima.ml;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.Feature;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Lists.transform;
import static java.lang.String.format;

/**
 * A template for composite tiered sequence classifier implementation.
 * Subclasses have to:
 * <ul>
 * <li>initialize a list of tier IDs,</li>
 * <li>initialize underlying classifiers for each tier,</li>
 * <li>initialize a feature extractor.</li>
 * </ul>
 *
 * @author Rinat Gareev
 */
public abstract class AbstractTieredSequenceClassifier<I extends AnnotationFS>
        implements TieredSequenceClassifier<I, String> {

    protected List<String> tierIds;
    protected List<org.cleartk.ml.SequenceClassifier<String>> classifiers;
    protected TieredFeatureExtractor<I, String> featureExtractor;

    @Override
    public List<String[]> classify(JCas jCas, Annotation spanAnno, List<? extends I> seq)
            throws CleartkProcessingException {
        final int tierNum = classifiers.size();
        // create a feature set for each token
        List<FeatureSet> featSets = featureExtractor.extractCommonFeatures(jCas, spanAnno, seq);
        List<List<String>> resultLabels = newArrayListWithCapacity(seq.size());
        for (I ignored : seq) {
            resultLabels.add(Lists.<String>newArrayListWithExpectedSize(tierNum));
        }
        //
        for (int tier = 0; tier < tierNum; tier++) {
            featureExtractor.onBeforeTier(featSets, resultLabels, tier, jCas, spanAnno, seq);
            // invoke a classifier of the current tier
            List<List<Feature>> featValues = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<String> labelSeq = getClassifier(tier).classify(featValues);
            if (labelSeq.size() != resultLabels.size())
                throw new IllegalStateException(format(
                        "Expected outcomes: %s, actual: %s", resultLabels.size(), labelSeq.size()));
            Iterator<String> labelSeqIter = labelSeq.iterator();
            Iterator<List<String>> resultIter = resultLabels.iterator();
            while (labelSeqIter.hasNext()) {
                String tierLabel = labelSeqIter.next();
                List<String> resultLabel = resultIter.next();
                resultLabel.add(MLPackageUtils.isNullLabel(tierLabel) ? null : tierLabel);
            }
            // if not the last tier
            if (tier != tierNum - 1) {
                featureExtractor.onAfterTier(featSets, resultLabels, tier, jCas, spanAnno, seq);
            }
        }
        return new ArrayList<>(transform(resultLabels, new Function<List<String>, String[]>() {
            @Override
            public String[] apply(List<String> list) {
                return list.toArray(new String[list.size()]);
            }
        }));
    }

    @Override
    public List<String> getTierIds() {
        return tierIds;
    }

    @Override
    public void close() {
        for (org.cleartk.ml.SequenceClassifier<String> cl : classifiers)
            if (cl instanceof Closeable) {
                IOUtils.closeQuietly((Closeable) cl);
            }
    }

    private org.cleartk.ml.SequenceClassifier<String> getClassifier(int tier) {
        return classifiers.get(tier);
    }
}
