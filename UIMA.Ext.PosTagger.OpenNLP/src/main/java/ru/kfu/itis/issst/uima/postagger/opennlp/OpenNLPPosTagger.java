/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.coveredTextFunction;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import opennlp.model.AbstractModel;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.util.BeamSearch;
import opennlp.tools.util.Sequence;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.initializable.InitializableFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Collections2;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.morph.commons.TagMapper;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OpenNLPPosTagger extends JCasAnnotator_ImplBase {

	public static final String PARAM_TAG_MAPPER_CLASS = "tagMapperClass";
	public static final String PARAM_BEAM_SIZE = "beamSize";
	public static final String RESOURCE_POS_MODEL = "posModel";

	@ExternalResource(key = RESOURCE_POS_MODEL, mandatory = true)
	private POSModel modelAggregate;
	@ConfigurationParameter(name = PARAM_BEAM_SIZE, defaultValue = "3")
	private int beamSize;
	@ConfigurationParameter(name = PARAM_TAG_MAPPER_CLASS,
			defaultValue = "ru.kfu.itis.issst.uima.morph.commons.DictionaryBasedTagMapper")
	private String tagMapperClassName;
	// state
	private TagMapper tagMapper;
	private BeamSearch<String> beam;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		tagMapper = InitializableFactory.create(ctx, tagMapperClassName, TagMapper.class);
		//
		POSTaggerFactory factory = modelAggregate.getFactory();
		AbstractModel posModel = modelAggregate.getPosModel();
		POSContextGenerator contextGen = factory.getPOSContextGenerator(beamSize);
		beam = new BeamSearch<String>(beamSize, contextGen, posModel,
				factory.getSequenceValidator(), 0);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			process(jCas, sent);
		}
	}

	private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		Collection<Token> tokens = JCasUtil.select(jCas, Token.class);
		String[] tokenStrings = Collections2.transform(tokens, coveredTextFunction())
				.toArray(new String[tokens.size()]);
		Object[] tokenArr = tokens.toArray();
		Sequence bestOutSeq = beam.bestSequence(tokenStrings, tokenArr);
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
				Set<String> grams = tagMapper.parseTag(tag, token.getCoveredText());
				MorphDictionaryUtils.applyGrammems(grams, wf);
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
