/**
 *
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import opennlp.model.AbstractModel;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.initializable.InitializableFactory;
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
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.commons.TagUtils.postProcessExternalTag;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class OpenNLPPosTagger extends JCasAnnotator_ImplBase {

    public static final String PARAM_BEAM_SIZE = "beamSize";
    public static final String PARAM_SEQUENCE_VALIDATOR_CLASS = "sequenceValidatorClass";
    public static final String RESOURCE_POS_MODEL = "posModel";
    public static final String DEFAULT_SEQUENCE_VALIDATOR_CLASS =
            "ru.kfu.itis.issst.uima.postagger.opennlp.PunctuationTokenSequenceValidator";

    /**
     * Create a resource description with the specified parameter values. The
     * results declares the mandatory dependency on a resource with
     * {@link OpenNLPModelHolder} API and resource key
     * {@value #RESOURCE_POS_MODEL}.
     *
     * @param sequenceValidatorClass class name of a sequence validator. If null then default one (
     *                               {@value #DEFAULT_SEQUENCE_VALIDATOR_CLASS}) will be used.
     * @param beamSize               beam size. If null them the default value will be used.
     * @return description instance
     * @throws ResourceInitializationException
     */
    public static AnalysisEngineDescription createDescription(
            String sequenceValidatorClass,
            Integer beamSize)
            throws ResourceInitializationException {
        // prepare TypeSystemDescriptor consisting of produced types
        return AnalysisEngineFactory.createEngineDescription(OpenNLPPosTagger.class,
                PosTaggerAPI.getTypeSystemDescription(),
                PARAM_BEAM_SIZE, beamSize,
                PARAM_SEQUENCE_VALIDATOR_CLASS, sequenceValidatorClass);
    }

    @ExternalResource(key = RESOURCE_POS_MODEL, mandatory = true)
    private OpenNLPModelHolder<POSModel> modelAggregateHolder;
    @ConfigurationParameter(name = PARAM_BEAM_SIZE, defaultValue = "3", mandatory = false)
    private int beamSize;
    @ConfigurationParameter(name = PARAM_SEQUENCE_VALIDATOR_CLASS, mandatory = false,
            defaultValue = DEFAULT_SEQUENCE_VALIDATOR_CLASS)
    private String sequenceValidatorClassName;
    // state
    private POSModel modelAggregate;
    private SequenceValidator<Token> sequenceValidator;
    private BeamSearch<Token> beam;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        //
        modelAggregate = modelAggregateHolder.getModel();
        //
        if (sequenceValidatorClassName != null) {
            sequenceValidator = InitializableFactory.create(ctx, sequenceValidatorClassName,
                    SequenceValidator.class);
        }
        //
        POSTaggerFactory factory = modelAggregate.getFactory();
        AbstractModel posModel = modelAggregate.getPosModel();
        BeamSearchContextGenerator<Token> contextGen = factory.getContextGenerator();
        beam = new BeamSearch<Token>(beamSize, contextGen, posModel, sequenceValidator, 0);
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
            process(jCas, sent);
        }
    }

    private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
        Collection<Token> tokens = JCasUtil.selectCovered(jCas, Token.class, sent);
        Token[] tokenArr = tokens.toArray(new Token[tokens.size()]);
        Sequence bestOutSeq = beam.bestSequence(tokenArr, new Object[]{sent});
        if (bestOutSeq == null) {
            getLogger().warn(String.format("Can't infer best sequence for sentence in %s:\n%s",
                    getDocumentUri(jCas), toPrettyString(sent)));
            return;
        }
        List<String> out = bestOutSeq.getOutcomes();
        if (out.size() != tokens.size()) {
            throw new IllegalStateException(String.format("InputSeq.size != OutputSeq.size"));
        }
        Iterator<Token> tokenIter = tokens.iterator();
        Iterator<String> outIter = out.iterator();
        while (tokenIter.hasNext()) {
            Token token = tokenIter.next();
            String tag = postProcessExternalTag(outIter.next());
            if (isWordTag(token, tag)) {
                Word word = new Word(jCas, token.getBegin(), token.getEnd());
                word.setToken(token);

                Wordform wf = new Wordform(jCas);
                wf.setWord(word);
                wf.setPos(tag);
                String[] gramArr = splitIntoGrammemes(tag);
                if (gramArr != null) {
                    wf.setGrammems(FSUtils.toStringArray(jCas, gramArr));
                }
                word.setWordforms(FSUtils.toFSArray(jCas, wf));

                word.addToIndexes();
            }
        }
    }

    private boolean isWordTag(Token token, String tag) {
        // TODO check whether tag is a punctuation tag
        return token instanceof NUM || token instanceof W;
    }

    private String[] splitIntoGrammemes(String tag) {
        if (StringUtils.isEmpty(tag)) return null;
        // TODO should we read delimiter from the serialized model?
        // TODO at least read the char constant from GramModelBasedTagMapper
        return StringUtils.split(tag, '&');
    }
}
