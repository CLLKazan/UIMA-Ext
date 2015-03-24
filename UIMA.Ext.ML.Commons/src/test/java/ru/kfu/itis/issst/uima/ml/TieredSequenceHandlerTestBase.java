package ru.kfu.itis.issst.uima.ml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.ml.Feature;
import org.cleartk.ml.feature.extractor.FeatureExtractor1;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;
import static java.lang.String.format;

/**
 * @author Rinat Gareev
 */
public class TieredSequenceHandlerTestBase {
    @Mock
    protected FeatureExtractor1 commonFeatExtractor;
    @Mock
    private FeatureExtractor1 fe1;
    @Mock
    private FeatureExtractor1 fe2;
    @Mock
    private FeatureExtractor1 fe3;

    protected static ArgumentMatcher<List<List<Feature>>> unorderedFeatures(final List<Set<String>> expectedValues) {
        return new ArgumentMatcher<List<List<Feature>>>() {
            @Override
            public boolean matches(Object argument) {
                @SuppressWarnings("unchecked") List<List<Feature>> actual = (List<List<Feature>>) argument;
                List<Set<String>> actualAsSets = new ArrayList<>(Lists.transform(actual, new Function<List<Feature>, Set<String>>() {
                    @Override
                    public Set<String> apply(List<Feature> input) {
                        Set<String> result = Sets.newHashSet();
                        for (Feature f : input) {
                            result.add((String) f.getValue());
                        }
                        return result;
                    }
                }));
                return actualAsSets.equals(expectedValues);
            }
        };
    }

    protected FeatureExtractor1 getTierSpecificFeatureExtractor(int tier) {
        switch (tier) {
            case 0:
                return fe1;
            case 1:
                return fe2;
            case 2:
                return fe3;
        }
        throw new IllegalStateException();
    }

    protected class TestTieredFeatureExtractor implements TieredFeatureExtractor<Annotation, String> {
        @Override
        public void initialize(Properties cfg) throws ResourceInitializationException {
        }

        @Override
        public void onBeforeTier(List<FeatureSet> featSets, List<List<String>> labels, int tier,
                                 JCas jCas, Annotation spanAnno, List<? extends Annotation> tokens) {
            for (int tokPos = 0; tokPos < tokens.size(); tokPos++) {
                FeatureSet tokFeatSet = featSets.get(tokPos);
                tokFeatSet.add(of(new Feature(format("tier%s-%s", tier, tokPos))),
                        getTierSpecificFeatureExtractor(tier));
            }
        }

        @Override
        public void onAfterTier(List<FeatureSet> featSets, List<List<String>> labels, int tier,
                                JCas jCas, Annotation spanAnno, List<? extends Annotation> tokens) {
            for (FeatureSet tokFeatSet : featSets) {
                tokFeatSet.removeFeaturesBySource(ImmutableSet.of(getTierSpecificFeatureExtractor(tier)));
            }
        }

        @Override
        public List<FeatureSet> extractCommonFeatures(
                JCas jCas, Annotation spanAnno, List<? extends Annotation> tokens) {
            List<FeatureSet> resultList = Lists.newArrayList();
            for (int tokPos = 0; tokPos < tokens.size(); tokPos++) {
                FeatureSet fs = FeatureSets.empty();
                fs.add(of(new Feature("common-feature-" + tokPos)), commonFeatExtractor);
                resultList.add(fs);
            }
            return resultList;
        }
    }
}
