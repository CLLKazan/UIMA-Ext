/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static org.uimafit.factory.ExternalResourceFactory.bindExternalResource;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.commons.TagUtils.postProcessExternalTag;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import opennlp.model.AbstractModel;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.initializable.InitializableFactory;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OpenNLPPosTagger extends JCasAnnotator_ImplBase {

	public static final String PARAM_BEAM_SIZE = "beamSize";
	public static final String PARAM_SEQUENCE_VALIDATOR_CLASS = "sequenceValidatorClass";
	public static final String RESOURCE_POS_MODEL = "posModel";
	public static final String DEFAULT_SEQUENCE_VALIDATOR_CLASS =
			"ru.kfu.itis.issst.uima.postagger.opennlp.PunctuationTokenSequenceValidator";

	public static AnalysisEngineDescription createDescription(
			ExternalResourceDescription modelDesc,
			String sequenceValidatorClass,
			Integer beamSize)
			throws ResourceInitializationException {
		// prepare TypeSystemDescriptor consisting of produced types
		TypeSystemDescription tsDesc = createTypeSystemDescription("org.opencorpora.morphology-ts");
		return AnalysisEngineFactory.createPrimitiveDescription(OpenNLPPosTagger.class, tsDesc,
				RESOURCE_POS_MODEL, modelDesc,
				PARAM_BEAM_SIZE, beamSize,
				PARAM_SEQUENCE_VALIDATOR_CLASS, sequenceValidatorClass);
	}

	/**
	 * Create description for this tagger with the specified model, sequence
	 * validator implementation, beam size and optional injection of morph
	 * dictionary.
	 * 
	 */
	public static AnalysisEngineDescription createDescription(String modelUrl,
			String sequenceValidatorClass, Integer beamSize,
			ExternalResourceDescription morphDictDesc)
			throws ResourceInitializationException {
		ExternalResourceDescription modelDesc = createExternalResourceDescription(
				DefaultPOSModelHolder.class, modelUrl);
		if (morphDictDesc != null) {
			bindExternalResource(modelDesc,
					DefaultPOSModelHolder.RESOURCE_MORPH_DICT, morphDictDesc);
		}
		return createDescription(modelDesc, sequenceValidatorClass, beamSize);
	}

	/**
	 * Create description for this tagger with the specified model, optional
	 * injection of morph dictionary, the default value of beam size and the
	 * default implementation of sequence validator.
	 * 
	 */
	public static AnalysisEngineDescription createDescription(String modelUrl,
			ExternalResourceDescription morphDictDesc)
			throws ResourceInitializationException {
		return createDescription(modelUrl, null, null, morphDictDesc);
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
		Sequence bestOutSeq = beam.bestSequence(tokenArr, null);
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
			String tag = outIter.next();
			if (isWordTag(token, tag)) {
				Word word = new Word(jCas, token.getBegin(), token.getEnd());
				word.setToken(token);

				Wordform wf = new Wordform(jCas);
				wf.setWord(word);
				wf.setPos(postProcessExternalTag(tag));
				word.setWordforms(FSUtils.toFSArray(jCas, wf));

				word.addToIndexes();
			}
		}
	}

	private boolean isWordTag(Token token, String tag) {
		// TODO check whether tag is a punctuation tag
		return token instanceof NUM || token instanceof W;
	}
}
