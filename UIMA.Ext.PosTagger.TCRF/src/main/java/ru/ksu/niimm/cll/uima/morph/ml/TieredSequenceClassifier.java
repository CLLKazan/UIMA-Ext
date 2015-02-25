package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.Lists;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import java.util.List;

import static com.google.common.collect.Lists.newArrayListWithCapacity;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceClassifier<OUT> extends SequenceClassifier<OUT> {

    private GramTiers gramTiers;

    @Override
    public List<OUT> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq) {
        // create a feature set for each token
        List<FeatureSet> featSets = newArrayListWithCapacity(seq.size());
        for (FeatureStructure _tok : seq) {
            // TODO
            Token tok = (Token) _tok;
            featSets.add(extractCommonFeatures(jCas, spanAnno, tok));
        }
        //
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
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
            List<OUT> labelSeq = getClassifier(tier).classify(featValues);
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
}
