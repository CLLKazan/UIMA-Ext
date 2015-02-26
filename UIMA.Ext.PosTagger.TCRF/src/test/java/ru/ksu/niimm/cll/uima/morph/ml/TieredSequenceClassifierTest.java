package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.*;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;

/**
 * @author Rinat Gareev
 */
@RunWith(MockitoJUnitRunner.class)
public class TieredSequenceClassifierTest {
    @Mock
    private org.cleartk.classifier.SequenceClassifier<String> classifier1;
    @Mock
    private org.cleartk.classifier.SequenceClassifier<String> classifier2;
    @Mock
    private org.cleartk.classifier.SequenceClassifier<String> classifier3;
    @Mock
    private SimpleFeatureExtractor commonFeatExtractor;
    @Mock
    private SimpleFeatureExtractor fe1;
    @Mock
    private SimpleFeatureExtractor fe2;
    @Mock
    private SimpleFeatureExtractor fe3;

    @Test
    public void testCorrectFeatureFlow() throws CleartkProcessingException {
        TieredSequenceClassifier cl = new TestTieredSequenceClassifier();
        // mock
        when(classifier1.classify(argThat(
                unorderedFeatures(of((Set<String>) Sets.newHashSet("common-feature", "tier0"))))))
                .thenReturn(of("First"));
        when(classifier2.classify(argThat(
                unorderedFeatures(of((Set<String>) Sets.newHashSet("common-feature", "tier1"))))))
                .thenReturn(of("Second"));
        when(classifier3.classify(argThat(
                unorderedFeatures(of((Set<String>) Sets.newHashSet("common-feature", "tier2"))))))
                .thenReturn(of("Third"));
        // invoke
        List<String> out = cl.classify(mock(JCas.class), mock(Annotation.class), of(mock(Token.class)));
        // verify
        assertEquals(1, out.size());
        assertEquals("First&Second&Third", out.get(0));
    }

    private class TestTieredSequenceClassifier extends TieredSequenceClassifier {
        TestTieredSequenceClassifier() {
            this.classifiers = of(classifier1, classifier2, classifier3);
        }

        @Override
        protected void onBeforeTier(List<FeatureSet> featSets, int tier,
                                    JCas jCas, Annotation spanAnno, List<Token> tokens) {
            for (FeatureSet tokFeatSet : featSets) {
                tokFeatSet.add(of(new Feature("tier" + tier)), getTierSpecificFeatureExtractor(tier));
            }
        }

        @Override
        protected void onAfterTier(List<FeatureSet> featSets, List<String> tierOutLabels, int tier,
                                   JCas jCas, Annotation spanAnno, List<Token> tokens) {
            for (FeatureSet tokFeatSet : featSets) {
                tokFeatSet.removeFeaturesBySource(ImmutableSet.of(getTierSpecificFeatureExtractor(tier)));
            }
        }

        @Override
        protected List<FeatureSet> extractCommonFeatures(JCas jCas, Annotation spanAnno, List<Token> tokens) {
            List<FeatureSet> resultList = Lists.newArrayList();
            for (Token tok : tokens) {
                FeatureSet fs = FeatureSets.empty();
                fs.add(of(new Feature("common-feature")), commonFeatExtractor);
                resultList.add(fs);
            }
            return resultList;
        }

        @Override
        public void close() {
        }
    }

    private static ArgumentMatcher<List<List<Feature>>> unorderedFeatures(final List<Set<String>> expectedValues) {
        return new ArgumentMatcher<List<List<Feature>>>() {
            @Override
            public boolean matches(Object argument) {
                List<List<Feature>> actual = (List<List<Feature>>) argument;
                List<Set<String>> actualAsSets = new ArrayList<Set<String>>(Lists.transform(actual, new Function<List<Feature>, Set<String>>() {
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

    private SimpleFeatureExtractor getTierSpecificFeatureExtractor(int tier) {
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
}
