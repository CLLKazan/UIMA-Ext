package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.metadata.ResourceManagerConfiguration;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.ResourceManagerConfiguration_impl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.ml.TieredSequenceDataWriter;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.test.AnnotationMatchers;
import ru.kfu.itis.issst.uima.test.TestCasBuilder;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import static org.hamcrest.Matchers.arrayContaining;
import static org.mockito.Mockito.*;
import static ru.kfu.itis.issst.uima.test.AnnotationMatchers.coverTextList;
import static ru.ksu.niimm.cll.uima.morph.ml.PTestUtils.list;

/**
 * @author Rinat Gareev
 */
public class PosSequenceTrainingDataExtractorTest {

    private static final TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
            TokenizerAPI.TYPESYSTEM_TOKENIZER,
            SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
            PosTaggerAPI.TYPESYSTEM_POSTAGGER);

    @Mock
    private TieredSequenceDataWriter<Token, String> dataWriterMock;
    private AnalysisEngine ae;

    @Before
    public void init() throws UIMAException {
        MockitoAnnotations.initMocks(this);
        when(dataWriterMock.getTierIds()).thenReturn(ImmutableList.of("POST", "NUMBER&CASE", "ANIM"));
        AnalysisEngineDescription aeDesc = AnalysisEngineFactory.createEngineDescription(
                PosSequenceTrainingDataExtractor.class, tsd);
        aeDesc.getAnalysisEngineMetaData().setName("AE");
        ResourceManager resMgr = UIMAFramework.newDefaultResourceManager();
        //
        ResourceManagerConfiguration resMgrCfg = new ResourceManagerConfiguration_impl();
        ExternalResourceDescription gramModelDesc = TestGramModel.createDesc();
        gramModelDesc.setName("testGramModel");
        resMgrCfg.addExternalResource(gramModelDesc);
        ExternalResourceFactory.bindExternalResource(resMgrCfg,
                PosSequenceTrainingDataExtractor.RESOURCE_GRAM_MODEL, "testGramModel");
        //
        ExternalResourceDescription dwDesc = MockedSequenceDataWriter.createDescription();
        dwDesc.setName("mockedDataWriter");
        resMgrCfg.addExternalResource(dwDesc);
        ExternalResourceFactory.bindExternalResource(resMgrCfg,
                PosSequenceTrainingDataExtractor.RESOURCE_DATA_WRITER, "mockedDataWriter");
        resMgr.initializeExternalResources(resMgrCfg, "/", Maps.<String, Object>newHashMap());
        @SuppressWarnings("unchecked")
        MockedSequenceDataWriter<Token, String> dw = (MockedSequenceDataWriter<Token, String>) resMgr.getResource(
                "/" + PosSequenceTrainingDataExtractor.RESOURCE_DATA_WRITER);
        dw.setMock(dataWriterMock);
        //
        ae = UIMAFramework.produceAnalysisEngine(aeDesc, resMgr, Maps.<String, Object>newHashMap());
    }

    @Test
    public void test() throws UIMAException {
        JCas cas = ae.newJCas();
        new TestCasBuilder(cas)
                .word("Жилино", "N", "nomn", "sing")
                .punct("—")
                .word("село", "N", "nomn", "sing")
                .word("в", "PREP")
                .word("Шуменской", "ADJF", "loct", "sing")
                .word("области", "N", "loct", "sing")
                .sentenceEnd(".")
                .word("Отправился", "V")
                .word("на", "PREP")
                .word("поиски", "N", "accs", "plur")
                .word("Европы", "N", "gent", "sing", "anim")
                .word("вместе", "PREP")
                .word("с", "PREP")
                .word("Кадмом", "N", "inst", "sing", "anim")
                .sentenceEnd(".")
                .apply();
        // mock
        // invoke
        ae.process(cas);
        // verify
        verify(dataWriterMock).write(eq(cas),
                argThat(AnnotationMatchers.<Annotation>fromTo("Жилино", "области .")),
                argThat(coverTextList(Token.class, "Жилино", "—", "село", "в", "Шуменской", "области", ".")),
                argThat(list(
                        arrayContaining("N", "sing&nomn", null),
                        arrayContaining("_P_", "_P_", "_P_"),
                        arrayContaining("N", "sing&nomn", null),
                        arrayContaining("PREP", null, null),
                        arrayContaining("ADJF", "sing&loct", null),
                        arrayContaining("N", "sing&loct", null),
                        arrayContaining("_P_", "_P_", "_P_")
                ))
        );
        verify(dataWriterMock).write(eq(cas),
                argThat(AnnotationMatchers.<Annotation>fromTo("Отправился", "Кадмом .")),
                argThat(coverTextList(Token.class, "Отправился", "на", "поиски", "Европы", "вместе", "с", "Кадмом", ".")),
                argThat(list(
                        arrayContaining("V", null, null),
                        arrayContaining("PREP", null, null),
                        arrayContaining("N", "plur&accs", null),
                        arrayContaining("N", "sing&gent", "anim"),
                        arrayContaining("PREP", null, null),
                        arrayContaining("PREP", null, null),
                        arrayContaining("N", "sing&inst", "anim"),
                        arrayContaining("_P_", "_P_", "_P_")
                ))
        );
    }
}
