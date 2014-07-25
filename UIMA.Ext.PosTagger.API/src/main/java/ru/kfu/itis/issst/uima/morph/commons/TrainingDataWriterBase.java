/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

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
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(multipleDeploymentAllowed = false)
public abstract class TrainingDataWriterBase extends JCasAnnotator_ImplBase {

	public static final String PARAM_OUTPUT_DIR = "outputDir";
	public static final String TRAINING_DATA_FILENAME = "training-data.txt";

	// config
	@ConfigurationParameter(name = PARAM_OUTPUT_DIR, mandatory = true)
	protected File outputDir;
	// state fields
	protected PrintWriter outputWriter;
	// per-CAS state fields
	private Map<Token, Word> token2WordIndex;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		//
		File outputFile = new File(outputDir, TRAINING_DATA_FILENAME);
		try {
			OutputStream os = FileUtils.openOutputStream(outputFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
			outputWriter = new PrintWriter(bw);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		try {
			token2WordIndex = MorphCasUtils.getToken2WordIndex(jCas);
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
		processSentence(jCas, tokens);
	}

	protected abstract void processSentence(JCas jCas, List<Token> tokens)
			throws AnalysisEngineProcessException;

	protected Word getWordOfToken(Token token) {
		return token2WordIndex.get(token);
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
