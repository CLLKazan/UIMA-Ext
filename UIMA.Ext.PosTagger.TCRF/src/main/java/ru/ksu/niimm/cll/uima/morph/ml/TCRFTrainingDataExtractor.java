/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.SequenceClassifier;
import org.cleartk.classifier.*;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.util.ResourceTicket;
import ru.kfu.itis.issst.uima.ml.WordAnnotator;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;
import ru.kfu.itis.issst.uima.morph.model.Grammeme;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import java.util.*;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryUtils.toGramBits;
import static ru.kfu.itis.issst.uima.postagger.PosTaggerAPI.DEFAULT_REUSE_EXISTING_WORD_ANNOTATIONS;
import static ru.kfu.itis.issst.uima.postagger.PosTaggerAPI.PARAM_REUSE_EXISTING_WORD_ANNOTATIONS;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class TCRFTrainingDataExtractor extends JCasAnnotator_ImplBase {

    public static final String RESOURCE_GRAM_MODEL = "gramModel";
    public static final String RESOURCE_CLASSIFIER_PACK = "classifiers";
    public static final String RESOURCE_DATA_WRITERS_FACTORY = "dataWriters";
    public static final String PARAM_TIERS = "tiers";

    // config fields
    @ExternalResource(key = RESOURCE_CLASSIFIER_PACK, mandatory = false)
    private SeqClassifierPack<String> classifierPack;
    @ExternalResource(key = RESOURCE_DATA_WRITERS_FACTORY, mandatory = false)
    private SequenceDataWriterPack<String> dataWriterPack;
    @ExternalResource(key = RESOURCE_GRAM_MODEL, mandatory = true)
    private GramModelHolder gramModelHolder;
    private GramModel gramModel;
    @ConfigurationParameter(name = PARAM_TIERS, mandatory = true)
    private List<String> tierDefs;

    @ConfigurationParameter(name = PARAM_REUSE_EXISTING_WORD_ANNOTATIONS,
            defaultValue = DEFAULT_REUSE_EXISTING_WORD_ANNOTATIONS)
    private boolean reuseExistingWordAnnotations;
    // derived config fields
    private Boolean training = null;
    private GramTiers gramTiers;
    // aggregate
    private IncrementalFeatureExtractor featureExtractor;
    private ResourceTicket classifierPackTicket;
    private ResourceTicket dataWriterPackTicket;
    // per-CAS state fields
    private Map<Token, Word> token2WordIndex;

    @Override
    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        gramModel = gramModelHolder.getGramModel();
        // check grammems
        checkDictGrammems();
        // determine training mode
        if (classifierPack != null) {
            if (dataWriterPack != null)
                throw new IllegalStateException("Both classifiers and data writers were set!");
            training = false;
            classifierPackTicket = classifierPack.acquire();
        } else if (dataWriterPack != null) {
            training = true;
            dataWriterPackTicket = dataWriterPack.acquire();
        } else {
            throw new IllegalStateException("No classifier or data writers were set");
        }
        if(training) {
            gramTiers = GramTiersFactory.parseGramTiers(gramModel, tierDefs);
        } else {

        }
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        if (!isTraining()) {
            if (reuseExistingWordAnnotations) {
                // clean wordforms to avoid erroneous feature extraction or output assignment
                cleanWordforms(jCas);
            } else {
                // ensure that there are no existing annotations
                // // otherwise things may go irregularly
                if (JCasUtil.exists(jCas, Word.class)) {
                    throw new IllegalStateException(String.format(
                            "CAS '%s' has Word annotations before this annotator",
                            getDocumentUri(jCas)));
                }
                // make Word annotations
                WordAnnotator.makeWords(jCas);
            }
        }
        token2WordIndex = MorphCasUtils.getToken2WordIndex(jCas);
        try {
            for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
                process(jCas, sent);
            }
        } finally {
            token2WordIndex.clear();
        }
    }

    private boolean isTraining() {
        return training;
    }

    @Override
    public void destroy() {
        IOUtils.closeQuietly(classifierPackTicket);
        IOUtils.closeQuietly(dataWriterPackTicket);
        super.destroy();
    }

    @Override
    public void collectionProcessComplete() throws AnalysisEngineProcessException {
        if (isTraining()) {
            IOUtils.closeQuietly(dataWriterPackTicket);
        }
        super.collectionProcessComplete();

    }

    private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
        if (isTraining()) {
            trainingProcess(jCas, sent);
        } else {
            taggingProcess(jCas, sent);
        }
    }

    private void trainingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
        // extract sentence tokens
        List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sent);
        if (tokens.isEmpty()) return;
        // extract sentence wordforms
        List<Wordform> wfSeq = newArrayListWithCapacity(tokens.size());
        for (Token token : tokens) {
            Word word = token2WordIndex.get(token);
            if (word == null) {
                wfSeq.add(null);
            } else {
                Wordform tokWf = MorphCasUtils.requireOnlyWordform(word);
                wfSeq.add(tokWf);
            }
        }
        //
        List<FeatureSet> featSets = newArrayListWithCapacity(tokens.size());
        for (Token tok : tokens) {
            featSets.add(FeatureSets.empty());
        }
        //
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            List<String> outLabels = newArrayListWithCapacity(tokens.size());
            // prepare feature values and extract output labels
            for (int tokIdx = 0; tokIdx < tokens.size(); tokIdx++) {
                Token tok = tokens.get(tokIdx);
                FeatureSet tokFeatSet = featSets.get(tokIdx);
                // TODO should know about cur tier?
                featureExtractor.extractNext(jCas, sent, tok, tokFeatSet);
                outLabels.add(extractOutputLabel(tier, jCas, tok));
            }
            // write as training data
            List<List<Feature>> featValues = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<Instance<String>> instances = Instances.toInstances(outLabels, featValues);
            getTrainingDataWriter(tier).write(instances);
        }
    }

    private void taggingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
        // extract sentence tokens
        List<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sent);
        if (tokens.isEmpty()) return;
        // extract sentence wordforms
        List<Wordform> wfSeq = newArrayListWithCapacity(tokens.size());
        for (Token token : tokens) {
            Word word = token2WordIndex.get(token);
            if (word == null) {
                wfSeq.add(null);
            } else {
                Wordform tokWf = MorphCasUtils.requireOnlyWordform(word);
                wfSeq.add(tokWf);
            }
        }
        // create a feature set for each token
        List<FeatureSet> featSets = newArrayListWithCapacity(tokens.size());
        for (Token tok : tokens) {
            featSets.add(FeatureSets.empty());
        }
        //
        for (int tier = 0; tier < gramTiers.getCount(); tier++) {
            // prepare feature values
            for (int tokIdx = 0; tokIdx < tokens.size(); tokIdx++) {
                Token tok = tokens.get(tokIdx);
                FeatureSet tokFeatSet = featSets.get(tokIdx);
                extractFeatures(tier, tokFeatSet, jCas, sent, tok);
            }
            // invoke a classifier of the current tier
            List<List<Feature>> featValues = Lists.transform(featSets, FeatureSets.LIST_FUNCTION);
            List<String> labelSeq = getClassifier(tier).classify(featValues);
            if (labelSeq.size() != wfSeq.size()) {
                throw new IllegalStateException();
            }
            if (!(labelSeq instanceof RandomAccess)) {
                labelSeq = new ArrayList<String>(labelSeq);
            }
            for (int i = 0; i < labelSeq.size(); i++) {
                String label = labelSeq.get(i);
                if (label == null || label.isEmpty() || label.equalsIgnoreCase("null")) {
                    // do nothing, it means there is no a new PoS-tag for this wordform
                    continue;
                }
                Wordform wf = wfSeq.get(i);
                if (wf == null) {
                    if (!label.equals(PunctuationUtils.OTHER_PUNCTUATION_TAG)) {
                        getLogger().warn(String.format(
                                "Classifier predicted the gram value for a non-word token: %s",
                                label));
                    }
                    // else - punctuation tag for punctuation token - OK
                } else if (label.equals(PunctuationUtils.OTHER_PUNCTUATION_TAG)) {
                    getLogger().warn("Classifier predicted the punctuation tag for a word token");
                } else {
                    Iterable<String> newGrams = targetGramSplitter.split(label);
                    MorphCasUtils.addGrammemes(jCas, wf, newGrams);
                }
            }
        }
    }

    private void extractFeatures(int tier, FeatureSet tokFeatSet, JCas jCas, Sentence sent, Token tok) {
        // TODO
        throw new UnsupportedOperationException();
    }

    private String extractOutputLabel(int tier, JCas jCas, Token token) {
        // classification label
        String outputLabel;
        Word word = token2WordIndex.get(token);
        if (word == null) {
            if (token instanceof NUM || token instanceof W) {
                throw new IllegalStateException(String.format(
                        "Token %s in %s does not have corresponding Word annotation",
                        toPrettyString(token), getDocumentUri(jCas)));
            }
            outputLabel = PunctuationUtils.OTHER_PUNCTUATION_TAG;
        } else {
            Wordform wf = MorphCasUtils.requireOnlyWordform(word);
            outputLabel = extractOutputLabel(tier, wf);
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
    private static final Splitter targetGramSplitter = Splitter.on(targetGramDelim);

    private SequenceClassifier<String> getClassifier(int tier) {
        throw new UnsupportedOperationException();
    }

    private SequenceDataWriter<String> getTrainingDataWriter(int tier) {
        return dataWriterPack.getDataWriter(tier);
    }

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

    private void cleanWordforms(JCas jCas) {
        for (Word w : JCasUtil.select(jCas, Word.class)) {
            Wordform wf = new Wordform(jCas);
            wf.setWord(w);
            w.setWordforms(FSUtils.toFSArray(jCas, wf));
        }
    }

}
