/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Joiner;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.ml.TieredSequenceDataWriter;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;
import ru.kfu.itis.issst.uima.morph.model.Grammeme;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryUtils.toGramBits;

/**
 * @author Rinat Gareev
 */
public class PosSequenceTrainingDataExtractor extends JCasAnnotator_ImplBase {

    public static final String RESOURCE_GRAM_MODEL = "gramModel";
    public static final String RESOURCE_DATA_WRITER = "dataWriter";

    // config fields
    @ExternalResource(key = RESOURCE_DATA_WRITER, mandatory = true)
    // String[] array means that for each token we have several (ordered) labels - one for each tier
    private TieredSequenceDataWriter<Token, String> dataWriter;
    @ExternalResource(key = RESOURCE_GRAM_MODEL, mandatory = true)
    private GramModelHolder gramModelHolder;
    private GramModel gramModel;

    // derived config fields
    private GramTiers gramTiers;

    // per-CAS state fields
    private Map<Token, Word> token2WordIndex;

    @Override
    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        gramModel = gramModelHolder.getGramModel();
        // check grammems
        checkDictGrammems();
        //
        gramTiers = GramTiersFactory.parseGramTiers(gramModel, dataWriter.getTierIds());
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        token2WordIndex = MorphCasUtils.getToken2WordIndex(jCas);
        try {
            for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
                process(jCas, sent);
            }
        } finally {
            token2WordIndex.clear();
        }
    }

    @Override
    public void collectionProcessComplete() throws AnalysisEngineProcessException {
        IOUtils.closeQuietly(dataWriter);
        super.collectionProcessComplete();

    }

    private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
        // extract sentence tokens
        List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sent);
        if (tokens.isEmpty()) return;
        // extract sentence wordforms
        List<String[]> seqLabels = newArrayListWithExpectedSize(tokens.size());
        for (Token token : tokens) {
            String[] tokenLabels = extractOutputLabel(jCas, token);
            seqLabels.add(tokenLabels);
        }
        //
        dataWriter.write(jCas, sent, tokens, seqLabels);
    }

    private String[] extractOutputLabel(JCas jCas, Token token) {
        // classification label
        String[] outputLabel = new String[gramTiers.getCount()];
        Word word = token2WordIndex.get(token);
        if (word == null) {
            if (token instanceof NUM || token instanceof W) {
                throw new IllegalStateException(String.format(
                        "Token %s in %s does not have corresponding Word annotation",
                        toPrettyString(token), getDocumentUri(jCas)));
            }
            Arrays.fill(outputLabel, PunctuationUtils.OTHER_PUNCTUATION_TAG);
        } else {
            Wordform wf = MorphCasUtils.requireOnlyWordform(word);
            for (int tier = 0; tier < gramTiers.getCount(); tier++) {
                outputLabel[tier] = extractOutputLabel(tier, wf);
            }
        }
        return outputLabel;
    }

    private String extractOutputLabel(int tier, Wordform wf) {
        BitSet wfBits = toGramBits(gramModel, FSUtils.toList(wf.getGrammems()));
        wfBits.and(gramTiers.getTierMask(tier));
        if (wfBits.isEmpty()) {
            return null;
        }
        return targetGramJoiner.join(gramModel.toGramSet(wfBits));
    }

    private static final String targetGramDelim = "&";
    private static final Joiner targetGramJoiner = Joiner.on(targetGramDelim);

    private void checkDictGrammems() {
        for (int grId = 0; grId < gramModel.getGrammemMaxNumId(); grId++) {
            Grammeme gr = gramModel.getGrammem(grId);
            if (gr != null && gr.getId().contains(targetGramDelim)) {
                throw new IllegalStateException(String.format(
                        "Grammeme %s contains character that is used as delimiter in this class",
                        gr.getId()));
            }
        }
    }
}
