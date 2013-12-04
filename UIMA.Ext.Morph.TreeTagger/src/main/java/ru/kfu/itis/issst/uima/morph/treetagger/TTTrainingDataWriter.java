/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

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

	public static final String PARAM_OUTPUT_FILE = "outputFile";
	public static final String PARAM_TAG_MAPPER_CLASS = "tagMapperClass";
	// config
	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true)
	private File outputFile;
	@ConfigurationParameter(name = PARAM_TAG_MAPPER_CLASS, defaultValue = "ru.kfu.itis.issst.uima.morph.treetagger.DictionaryBasedTagMapper")
	private String tagMapperClassName;
	// state fields
	private PrintWriter outputWriter;
	private TagMapper tagMapper;
	// per-CAS state fields
	private Map<Token, Word> token2WordIndex;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		tagMapper = InitializableFactory.create(ctx, tagMapperClassName, TagMapper.class);
		//
		try {
			OutputStream os = FileUtils.openOutputStream(outputFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
			outputWriter = new PrintWriter(bw, true);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
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
			for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
				process(jCas, sent);
			}
		} finally {
			token2WordIndex = null;
		}
	}

	private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		List<Token> tokens = JCasUtil.selectCovered(Token.class, sent);
		for (Token tok : tokens) {
			Word word = token2WordIndex.get(tok);
			if (word == null) {
				if (tok instanceof NUM || tok instanceof W) {
					getLogger().warn(String.format(
							"Token %s in %s does not have corresponding Word annotation",
							toPrettyString(tok), getDocumentUri(jCas)));
					continue;
				}
				writeTT(tok.getCoveredText(), tok.getCoveredText());
			} else {
				FSArray wfs = word.getWordforms();
				if (wfs == null || wfs.size() == 0) {
					throw new IllegalStateException(String.format(
							"No wordforms in Word %s in %s",
							toPrettyString(word), getDocumentUri(jCas)));
				}
				Wordform wf = (Wordform) wfs.get(0);
				String tag = tagMapper.toTag(wf);
				writeTT(tok.getCoveredText(), tag);
			}
		}
	}

	private void writeTT(String token, String tag) {
		outputWriter.println();
		StringBuilder sb = new StringBuilder(token).append('\t').append(tag);
		outputWriter.print(sb);
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		closeWriter();
		super.collectionProcessComplete();
	}

	@Override
	public void destroy() {
		closeWriter();
		super.destroy();
	}

	private void closeWriter() {
		if (outputWriter != null) {
			IOUtils.closeQuietly(outputWriter);
			outputWriter = null;
		}
	}
}