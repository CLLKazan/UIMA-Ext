/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.io.File;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.JCasUtil;

import ru.kfu.itis.cll.uima.wfstore.DefaultWordformStoreBuilder;
import ru.kfu.itis.cll.uima.wfstore.WordformStore;
import ru.kfu.itis.cll.uima.wfstore.WordformStoreBuilder;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(multipleDeploymentAllowed = false)
public class BaselineLearner extends BaselineAnnotator {

	public static final String PARAM_MODEL_OUTPUT_FILE = "modelOutputFile";

	// config fields
	@ConfigurationParameter(name = PARAM_MODEL_OUTPUT_FILE, mandatory = true)
	private File modelOutputFile;
	// derived
	private File modelDir;
	// state fields
	private WordformStoreBuilder<String> wfStoreBuilder;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		modelDir = modelOutputFile.getParentFile();
		if (modelDir == null) {
			// fallback to current directory
			modelDir = new File(".");
		}
		//
		wfStoreBuilder = new DefaultWordformStoreBuilder<String>();
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Word word : JCasUtil.select(jcas, Word.class)) {
			// check corpus word sanity
			Wordform corpusWf = MorphCasUtils.getOnlyWordform(word);
			if (corpusWf == null) {
				continue;
			}
			String corpusWfTag = String.valueOf(corpusWf.getPos());
			wfStoreBuilder.increment(word.getCoveredText(), corpusWfTag);
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		try {
			WordformStore<String> ws = wfStoreBuilder.build();
			ws.persist(modelOutputFile);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}