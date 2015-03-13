package ru.kfu.itis.issst.uima.ml;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ml.CleartkProcessingException;
import org.cleartk.ml.Feature;
import org.cleartk.ml.Instances;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayListWithExpectedSize;

/**
 * @author Rinat Gareev
 */
public abstract class AbstractTieredSequenceDataWriter<I extends AnnotationFS>
        implements TieredSequenceDataWriter<I, String> {

    protected List<String> tierIds;
    protected List<org.cleartk.ml.SequenceDataWriter<String>> dataWriters;
    protected TieredFeatureExtractor<I, String> featureExtractor;

    @Override
    public void write(JCas jCas, Annotation spanAnno,
                      List<? extends I> seq, List<String[]> seqCompositeLabels)
            throws CleartkProcessingException {
        //
        checkArgument(seqCompositeLabels.size() == seq.size());
        @SuppressWarnings("unchecked") List<I> tokens = (List<I>) seq;
        //
        List<FeatureSet> featSets = featureExtractor.extractCommonFeatures(jCas, spanAnno, tokens);
        // accumulate tier labels
        List<List<String>> labelBuilders = newArrayListWithExpectedSize(tokens.size());
        for (I ignored : tokens) {
            labelBuilders.add(Lists.<String>newArrayListWithExpectedSize(dataWriters.size()));
        }
        for (int tier = 0; tier < dataWriters.size(); tier++) {
            featureExtractor.onBeforeTier(featSets, labelBuilders, tier, jCas, spanAnno, tokens);
            //
            List<List<Feature>> seqFeatures = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<String> tierLabels = Lists.transform(seqCompositeLabels, getTierLabel(tier));
            org.cleartk.ml.SequenceDataWriter<String> tierDW = dataWriters.get(tier);
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (tierDW) {
                tierDW.write(Instances.toInstances(tierLabels, seqFeatures));
            }
            // if not the last tier
            if (tier != dataWriters.size() - 1) {
                // add cur tier labels for each token
                for (int token = 0; token < seqCompositeLabels.size(); token++) {
                    List<String> tokLabelBuilder = labelBuilders.get(token);
                    String[] tokCompositeLabel = seqCompositeLabels.get(token);
                    tokLabelBuilder.add(tokCompositeLabel[tier]);
                }
                featureExtractor.onAfterTier(featSets, labelBuilders, tier, jCas, spanAnno, tokens);
            }
        }
    }

    @Override
    public List<String> getTierIds() {
        return tierIds;
    }

    @Override
    public void close() throws IOException {
        for (org.cleartk.ml.SequenceDataWriter<String> dw : dataWriters)
            try {
                dw.finish();
            } catch (CleartkProcessingException e) {
                throw new IllegalStateException(e);
            }
    }

    private static Function<String[], String> getTierLabel(final int tier) {
        return new Function<String[], String>() {
            @Override
            public String apply(String[] input) {
                return input[tier];
            }
        };
    }
}
