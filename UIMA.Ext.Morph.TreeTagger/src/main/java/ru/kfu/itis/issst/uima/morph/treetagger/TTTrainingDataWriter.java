/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.kfu.itis.issst.uima.morph.treetagger.DictionaryToTTLexicon.OTHER_PUNCTUATION_TAG;
import static ru.kfu.itis.issst.uima.morph.treetagger.DictionaryToTTLexicon.punctuationTagMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.factory.initializable.InitializableFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(multipleDeploymentAllowed = false)
public class TTTrainingDataWriter extends JCasAnnotator_ImplBase {

	public static final String PARAM_OUTPUT_DIR = "outputFile";
	public static final String PARAM_TAG_MAPPER_CLASS = "tagMapperClass";
	public static final String TRAINING_DATA_FILENAME = "training-data.txt";
	public static final String LEXICON_FILENAME = "training-data.lex";
	// default TT sentence end tag 
	public static final String TAG_SENT = "SENT";
	private static final String SYNTHETIC_SENTENCE_END_TOKEN = ".";
	// config
	@ConfigurationParameter(name = PARAM_OUTPUT_DIR, mandatory = true)
	private File outputDir;
	@ConfigurationParameter(name = PARAM_TAG_MAPPER_CLASS, defaultValue = "ru.kfu.itis.issst.uima.morph.treetagger.DictionaryBasedTagMapper")
	private String tagMapperClassName;
	// state fields
	private PrintWriter outputWriter;
	private TagMapper tagMapper;
	private Multimap<String, String> outputLexicon;
	// statistics
	private int syntheticSentEnds;
	// per-CAS state fields
	private Map<Token, Word> token2WordIndex;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		tagMapper = InitializableFactory.create(ctx, tagMapperClassName, TagMapper.class);
		//
		File outputFile = new File(outputDir, TRAINING_DATA_FILENAME);
		try {
			OutputStream os = FileUtils.openOutputStream(outputFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
			outputWriter = new PrintWriter(bw);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		//
		outputLexicon = TreeMultimap.create();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			// prepare token2WordIndex
			token2WordIndex = Maps.newHashMap();
			for (Word word : JCasUtil.select(jCas, Word.class)) {
				Token token = (Token) word.getToken();
				if (token == null) {
					throw new IllegalStateException(String.format(
							"No token assigned for Word %s in %s",
							toPrettyString(word), getDocumentUri(jCas)));
				}
				if (token2WordIndex.put(token, word) != null) {
					throw new IllegalStateException(String.format(
							"Shared token for Word %s in %s",
							toPrettyString(word), getDocumentUri(jCas)));
				}
			}
			// process each sentence
			for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
				process(jCas, sent);
			}
		} finally {
			token2WordIndex = null;
		}
	}

	private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		List<Token> tokens = JCasUtil.selectCovered(Token.class, sent);
		if (tokens.isEmpty()) {
			return;
		}
		final Token lastTok = tokens.get(tokens.size() - 1);
		boolean hasSentenceEnd = false;
		for (Token tok : tokens) {
			Word word = token2WordIndex.get(tok);
			String tokStr = tok.getCoveredText();
			if (word == null) {
				if (tok instanceof NUM || tok instanceof W) {
					getLogger().warn(String.format(
							"Token %s in %s does not have corresponding Word annotation",
							toPrettyString(tok), getDocumentUri(jCas)));
					continue;
				}
				String tag;
				if (tok == lastTok) {
					// sentence end
					tag = TAG_SENT;
					hasSentenceEnd = true;
				} else {
					tag = punctuationTagMap.get(tokStr);
					if (tag == null) {
						tag = OTHER_PUNCTUATION_TAG;
					}
				}
				writeTT(tokStr, tag);
			} else {
				FSArray wfs = word.getWordforms();
				if (wfs == null || wfs.size() == 0) {
					throw new IllegalStateException(String.format(
							"No wordforms in Word %s in %s",
							toPrettyString(word), getDocumentUri(jCas)));
				}
				Wordform wf = (Wordform) wfs.get(0);
				String tag = tagMapper.toTag(wf);
				if (!isDigitalNumber(tokStr)) {
					// null means NONLEX
					if (tag != null) {
						outputLexicon.put(normalizeForLexicon(tokStr), tag);
					}
				}
				writeTT(tokStr, tag);
			}
		}
		if (!hasSentenceEnd) {
			writeTT(SYNTHETIC_SENTENCE_END_TOKEN, TAG_SENT);
			syntheticSentEnds++;
		}
	}

	private void writeTT(String token, String tag) {
		StringBuilder sb = new StringBuilder(token).append('\t').append(tag);
		outputWriter.println(sb);
	}

	private static String normalizeForLexicon(String str) {
		return str.toLowerCase();
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		getLogger().info("Synthetic sentence-end tokens were added: " + syntheticSentEnds);
		closeWriter();
		File lexiconFile = new File(outputDir, LEXICON_FILENAME);
		try {
			LexiconWriter.write(outputLexicon, lexiconFile);
		} catch (IOException e) {
			throw new AnalysisEngineProcessException(e);
		}
		outputLexicon = null;
		super.collectionProcessComplete();
	}

	@Override
	public void destroy() {
		closeWriter();
		outputLexicon = null;
		super.destroy();
	}

	private static final Pattern digitalNumberPattern = Pattern.compile("\\d+");

	private boolean isDigitalNumber(String tok) {
		return digitalNumberPattern.matcher(tok).matches();
	}

	private void closeWriter() {
		if (outputWriter != null) {
			IOUtils.closeQuietly(outputWriter);
			outputWriter = null;
		}
	}
}