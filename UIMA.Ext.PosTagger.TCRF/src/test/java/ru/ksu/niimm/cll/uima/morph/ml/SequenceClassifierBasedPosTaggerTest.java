package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static com.google.common.collect.ImmutableList.*;
import static ru.kfu.itis.issst.uima.test.AnnotationMatcher.coverText;
import static org.junit.Assert.*;

import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.util.JCasUtil;
import org.xml.sax.SAXException;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Rinat Gareev
 */
public class SequenceClassifierBasedPosTaggerTest {
    @Mock
    private SequenceClassifier<String> classifierMock;
    private AnalysisEngine ae;

    @Before
    public void before() throws ResourceInitializationException {
        MockitoAnnotations.initMocks(this);
        delegate = classifierMock;
        TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
                "ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
                TokenizerAPI.TYPESYSTEM_TOKENIZER,
                SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
                PosTaggerAPI.TYPESYSTEM_POSTAGGER
        );
        AnalysisEngineDescription taggerDesc = AnalysisEngineFactory.createPrimitiveDescription(
                SeqClassifierBasedPosTagger.class,
                tsd);
        // bind classifier resource
        ExternalResourceDescription classifierDesc = ExternalResourceFactory.createExternalResourceDescription(
                StaticSequenceClassifierWrapper.class, "file:pom.xml");
        ExternalResourceFactory.bindExternalResource(
                taggerDesc, SeqClassifierBasedPosTagger.RESOURCE_CLASSIFIER, classifierDesc);
        ae = UIMAFramework.produceAnalysisEngine(taggerDesc);
    }

    @Test
    public void testOnTest1Txt() throws UIMAException, IOException, SAXException {
        CAS cas = ae.newCAS();
        FileInputStream fin = FileUtils.openInputStream(new File("test-data/test1.txt.xmi"));
        try {
            XmiCasDeserializer.deserialize(fin, cas);
        } finally {
            IOUtils.closeQuietly(fin);
        }
        // stub
        when(classifierMock.classify(any(JCas.class),
                argThat(coverText("Хутор Графский находится в Курском районе Ставропольского края.", Annotation.class)),
                argThat(wordformsWithTokens("Хутор", "Графский", "находится", "в", "Курском", "районе",
                        "Ставропольского", "края", "."))
        )).thenReturn(of("N", "A&Named", "V", "PREP", "A&Named", "N", "A&Named", "N", "."));
        when(classifierMock.classify(any(JCas.class),
                argThat(coverText("Расстояние до краевого центра: 255 км.", Annotation.class)),
                argThat(wordformsWithTokens("Расстояние", "до", "краевого", "центра", ":", "255", "км", "."))
        )).thenReturn(of("N", "PREP", "A", "N", ":", "NUM", "N", "."));
        // invoke
        ae.process(cas);
        // verify
        List<Word> words = ImmutableList.copyOf(JCasUtil.select(cas.getJCas(), Word.class));
        List<String> actualTags = Lists.transform(words, MorphCasUtils.POS_TAG_FUNCTION);
        assertEquals(
                of("N", "A&Named", "V", "PREP", "A&Named", "N", "A&Named", "N", "N", "PREP", "A", "N", "NUM", "N"),
                actualTags);
        assertEquals(of("A", "Named"), FSUtils.toList(words.get(1).getWordforms(0).getGrammems()));
    }

    static SequenceClassifier<String> delegate;

    public static class StaticSequenceClassifierWrapper implements SequenceClassifier<String>, SharedResourceObject {

        @Override
        public List<String> classify(JCas jCas, Annotation spanAnno, List<? extends FeatureStructure> seq) {
            return delegate.classify(jCas, spanAnno, seq);
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public void load(DataResource data) throws ResourceInitializationException {
        }
    }

    public static Matcher<List<FeatureStructure>> wordformsWithTokens(final String... expectedTxts) {
        return new ArgumentMatcher<List<FeatureStructure>>() {
            @Override
            public boolean matches(Object arg) {
                if (arg == null) return false;
                List<FeatureStructure> list = (List<FeatureStructure>) arg;
                if (list.size() != expectedTxts.length)
                    return false;
                for (int i = 0; i < expectedTxts.length; i++) {
                    String txt = expectedTxts[i];
                    String actual;
                    if (list.get(i) instanceof Token) {
                        actual = ((Token) list.get(i)).getCoveredText();
                    } else {
                        actual = ((Wordform) list.get(i)).getWord().getCoveredText();
                    }
                    if (!txt.equals(actual))
                        return false;
                }
                return true;
            }
        };
    }
}
