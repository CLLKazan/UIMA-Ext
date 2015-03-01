package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.Instances;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Rinat Gareev
 */
public abstract class TieredSequenceDataWriter implements SequenceDataWriter<String[]> {

    protected List<org.cleartk.classifier.SequenceDataWriter<String>> dataWriters;
    protected TieredFeatureExtractor featureExtractor;

    @Override
    public void write(JCas jCas, Annotation spanAnno,
                      List<? extends FeatureStructure> seq, List<String[]> seqCompositeLabels)
            throws CleartkProcessingException {
        //
        checkArgument(seqCompositeLabels.size() == seq.size());
        @SuppressWarnings("unchecked") List<Token> tokens = (List<Token>) seq;
        //
        List<FeatureSet> featSets = featureExtractor.extractCommonFeatures(jCas, spanAnno, tokens);
        for (int tier = 0; tier < dataWriters.size(); tier++) {
            featureExtractor.onBeforeTier(featSets, tier, jCas, spanAnno, tokens);
            //
            List<List<Feature>> seqFeatures = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<String> tierLabels = Lists.transform(seqCompositeLabels, getTierLabel(tier));
            org.cleartk.classifier.SequenceDataWriter<String> tierDW = dataWriters.get(tier);
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (tierDW) {
                tierDW.write(Instances.toInstances(tierLabels, seqFeatures));
            }
            // if not the last tier
            if (tier != dataWriters.size() - 1) {
                featureExtractor.onAfterTier(featSets, tierLabels, tier, jCas, spanAnno, tokens);
            }
        }
    }

    @Override
    public void close() throws IOException {
        for (org.cleartk.classifier.SequenceDataWriter<String> dw : dataWriters)
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
