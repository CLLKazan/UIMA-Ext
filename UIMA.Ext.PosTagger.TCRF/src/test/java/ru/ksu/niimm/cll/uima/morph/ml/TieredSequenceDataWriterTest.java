package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.Instance;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

import static com.google.common.collect.ImmutableList.of;
import static org.mockito.Mockito.*;
import static ru.ksu.niimm.cll.uima.morph.ml.PTestUtils.list;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Rinat Gareev
 */
@RunWith(MockitoJUnitRunner.class)
public class TieredSequenceDataWriterTest extends TieredSequenceHandlerTestBase {

    @Mock
    private org.cleartk.classifier.SequenceDataWriter<String> dataWriter1;
    @Mock
    private org.cleartk.classifier.SequenceDataWriter<String> dataWriter2;
    @Mock
    private org.cleartk.classifier.SequenceDataWriter<String> dataWriter3;

    @Test
    public void testCorrectWorkflow() throws CleartkProcessingException {
        TieredSequenceDataWriter dw = new TestTieredSequenceDataWriter();
        // stub
        // invoke
        dw.write(mock(JCas.class), mock(Annotation.class), of(mock(Token.class), mock(Token.class)),
                of(new String[]{"out-first-token", null, null}, new String[]{null, "out-second-token", null}));
        // verify
        verify(dataWriter1).write(
                argThat(list(
                        // first instance
                        is(new Instance<String>("out-first-token", )),
                        // second instance
                        is(new Instance<String>()))));
        verify(dataWriter2).write(
                argThat(list(
                        // first instance
                        is(new Instance<String>()),
                        // second instance
                        is(new Instance<String>()))));
        verify(dataWriter3).write(
                argThat(list(
                        // first instance
                        is(new Instance<String>()),
                        // second instance
                        is(new Instance<String>()))));
    }

    private class TestTieredSequenceDataWriter extends TieredSequenceDataWriter {
        private TestTieredSequenceDataWriter() {
            this.dataWriters = of(dataWriter1, dataWriter2, dataWriter3);
            this.featureExtractor = new TestTieredFeatureExtractor();
        }
    }

    private class InstanceMatcher<OUT> extends TypeSafeMatcher<Instance<OUT>> {

        @Override
        protected boolean matchesSafely(Instance<OUT> item) {
            return false;
        }

        @Override
        public void describeTo(Description description) {

        }
    }
}
