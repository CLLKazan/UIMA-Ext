package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.Sets;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.CleartkProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Rinat Gareev
 */
@RunWith(MockitoJUnitRunner.class)
public class TieredSequenceClassifierTest extends TieredSequenceHandlerTestBase {
    @Mock
    private org.cleartk.classifier.SequenceClassifier<String> classifier1;
    @Mock
    private org.cleartk.classifier.SequenceClassifier<String> classifier2;
    @Mock
    private org.cleartk.classifier.SequenceClassifier<String> classifier3;

    @Test
    public void testCorrectFeatureFlow() throws CleartkProcessingException {
        TieredSequenceClassifier cl = new TestTieredSequenceClassifier();
        // mock
        when(classifier1.classify(argThat(
                unorderedFeatures(of((Set<String>) Sets.newHashSet("common-feature-0", "tier0-0"))))))
                .thenReturn(of("First"));
        when(classifier2.classify(argThat(
                unorderedFeatures(of((Set<String>) Sets.newHashSet("common-feature-0", "tier1-0"))))))
                .thenReturn(of("Second"));
        when(classifier3.classify(argThat(
                unorderedFeatures(of((Set<String>) Sets.newHashSet("common-feature-0", "tier2-0"))))))
                .thenReturn(of("Third"));
        // invoke
        List<String[]> out = cl.classify(mock(JCas.class), mock(Annotation.class), of(mock(Token.class)));
        // verify
        assertEquals(1, out.size());
        assertArrayEquals(new String[]{"First", "Second", "Third"}, out.get(0));
    }

    private class TestTieredSequenceClassifier extends TieredSequenceClassifier {
        TestTieredSequenceClassifier() {
            this.classifiers = of(classifier1, classifier2, classifier3);
            this.featureExtractor = new TestTieredFeatureExtractor();
        }
    }

}
