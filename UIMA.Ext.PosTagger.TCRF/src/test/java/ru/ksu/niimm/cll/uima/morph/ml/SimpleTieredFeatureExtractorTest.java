package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.util.DocumentUtils;
import ru.kfu.itis.issst.uima.ml.FeatureSet;
import ru.kfu.itis.issst.uima.morph.commons.DictionaryLoader4Tests;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.test.TestCasBuilder;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static org.uimafit.util.JCasUtil.select;
import static org.uimafit.util.JCasUtil.selectCovered;

/**
 * @author Rinat Gareev
 */
public class SimpleTieredFeatureExtractorTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
            DocumentUtils.TYPESYSTEM_COMMONS,
            TokenizerAPI.TYPESYSTEM_TOKENIZER,
            SentenceSplitterAPI.TYPESYSTEM_SENTENCES,
            PosTaggerAPI.TYPESYSTEM_POSTAGGER
    );
    private SimpleTieredFeatureExtractor fe;
    private JCas jCas;

    @Before
    public void initFE() {
        Properties feCfg = new Properties();
        feCfg.setProperty(SimpleTieredFeatureExtractor.CFG_GRAM_TIERS, "POST|NMbr|CAse");
        feCfg.setProperty(SimpleTieredFeatureExtractor.CFG_LEFT_CONTEXT_SIZE, "2");
        feCfg.setProperty(SimpleTieredFeatureExtractor.CFG_RIGHT_CONTEXT_SIZE, "1");
        fe = SimpleTieredFeatureExtractor.from(feCfg);
        DictionaryLoader4Tests.init();
        fe.initialize(DictionaryLoader4Tests.dict);
    }

    @Before
    public void initCas() throws ResourceInitializationException, CASException {
        CAS cas = CasCreationUtils.createCas(tsd, null, null);
        jCas = cas.getJCas();
    }

    @Test
    public void print1Test() throws CleartkExtractorException {
        // По всей видимости, прототип «Спорта» был разработан несколько раньше, чем «Kine Exakta».
        new TestCasBuilder(jCas)
                .word("По")
                .word("всей")
                .word("видимости")
                .punct(",")
                .word("прототип")
                .punct("«")
                .word("Спорта")
                .punct("»")
                .word("был")
                .word("разработан")
                .word("несколько")
                .word("раньше")
                .punct(",")
                .word("чем")
                .punct("«")
                .word("Kine")
                .word("Exakta")
                .punct("»")
                .sentenceEnd(".")
                .apply();
        // stub tier labels
        String[][] tierLabels = {
                {"PREP", "ADJF", "NOUN", ",", "NOUN", "\"", "NOUN", "\"", "VERB", "PRTS", "ADVB", "ADVB", ",", "CONJ", "\"", null, null, "\"", "."},
                {null, "sing", "sing", ",", "sing", "\"", "sing", "\"", "sing", "sing", null, null, ",", null, "\"", null, null, "\"", "."},
                {null, "datv", "datv", ",", "nomn", "\"", "nomn", "\"", null, null, null, null, ",", null, "\"", null, null, "\"", "."}
        };
        //
        Sentence sent = select(jCas, Sentence.class).iterator().next();
        ArrayList<Token> sentTokens = newArrayList(
                selectCovered(jCas, Token.class, sent));
        List<FeatureSet> sentFeatSets = fe.extractCommonFeatures(jCas, sent, sentTokens);
        log.debug("<<<Common feature>>>:\n{}", sentFeatSets);
        //
        int tier = 0;
        fe.onBeforeTier(sentFeatSets, compose(sentTokens.size()), tier, jCas, sent, sentTokens);
        log.debug("<<<Before tier {}>>>:\n{}", tier, sentFeatSets);
        fe.onAfterTier(sentFeatSets,
                compose(sentTokens.size(), tierLabels[0]),
                tier, jCas, sent, sentTokens);
        log.debug("<<<After tier {}>>>:\n{}", tier, sentFeatSets);
        //
        tier = 1;
        fe.onBeforeTier(sentFeatSets, compose(sentTokens.size(), tierLabels[0]), tier, jCas, sent, sentTokens);
        log.debug("<<<Before tier {}>>>:\n{}", tier, sentFeatSets);
        fe.onAfterTier(sentFeatSets,
                compose(sentTokens.size(), tierLabels[0], tierLabels[1]),
                tier, jCas, sent, sentTokens);
        log.debug("<<<After tier {}>>>:\n{}", tier, sentFeatSets);
        //
        fe.onBeforeTier(sentFeatSets, compose(sentTokens.size(), tierLabels[0], tierLabels[1]), tier, jCas, sent, sentTokens);
        log.debug("<<<Before tier {}>>>:\n{}", tier, sentFeatSets);
    }

    private static List<List<String>> compose(int tokNum, String[]... sourceArrays) {
        List<List<String>> result = newArrayList();
        for (int tok = 0; tok < tokNum; tok++) {
            List<String> tokElem = newArrayList();
            for (int tier = 0; tier < sourceArrays.length; tier++) {
                tokElem.add(sourceArrays[tier][tok]);
            }
            result.add(tokElem);
        }
        return result;
    }
}
