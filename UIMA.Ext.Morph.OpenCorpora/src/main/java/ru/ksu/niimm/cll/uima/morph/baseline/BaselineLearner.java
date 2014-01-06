/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.toGramBitSet;

import java.io.File;
import java.util.BitSet;
import java.util.Collection;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

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
	private WordformStoreBuilder wfStoreBuilder;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		modelDir = modelOutputFile.getParentFile();
		if (modelDir == null) {
			// fallback to current directory
			modelDir = new File(".");
		}
		//
		wfStoreBuilder = new DefaultWordformStoreBuilder();
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Word word : JCasUtil.select(jcas, Word.class)) {
			// check corpus word sanity
			if (word.getWordforms() == null) {
				continue;
			}
			Collection<Wordform> corpusWfs = FSCollectionFactory.create(
					word.getWordforms(),
					org.opencorpora.cas.Wordform.class);
			if (corpusWfs.isEmpty()) {
				continue;
			}
			if (corpusWfs.size() > 1) {
				getLogger().warn(String.format("Too much wordforms for word %s in %s",
						toPrettyString(word), getDocumentUri(jcas)));
			}
			Wordform corpusWf = corpusWfs.iterator().next();
			BitSet corpusWfGBS = toGramBitSet(dict, corpusWf);
			wfStoreBuilder.increment(word.getCoveredText(), corpusWfGBS);
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		try {
			WordformStore ws = wfStoreBuilder.build();
			ws.persist(modelOutputFile);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}