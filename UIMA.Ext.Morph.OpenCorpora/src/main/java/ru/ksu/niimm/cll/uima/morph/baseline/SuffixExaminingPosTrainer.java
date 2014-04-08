/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.normalizeToDictionary;
import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.toGramBitSet;

import java.io.File;
import java.util.BitSet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.JCasUtil;

import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(multipleDeploymentAllowed = false)
public class SuffixExaminingPosTrainer extends SuffixExaminingPosAnnotator {

	public static final String PARAM_WFSTORE_FILE = "wfStoreFile";

	// config fields
	@ConfigurationParameter(name = PARAM_WFSTORE_FILE, mandatory = true)
	private File wsFile;
	// state fields
	private WordformStoreBuilder wsBuilder;
	private int wordsExamined;
	private int shortWordsExamined;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		if (suffixLength <= 0) {
			throw new IllegalStateException("PARAM_SUFFIX_LENGTH is not specified");
		}
		wsBuilder = new DefaultWordformStoreBuilder();
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Word word : JCasUtil.select(jCas, Word.class)) {
			// check corpus word sanity
			Wordform corpusWf = MorphCasUtils.getOnlyWordform(word);
			if (corpusWf == null) {
				continue;
			}
			String wordStr = normalizeToDictionary(word.getCoveredText());
			BitSet corpusWfBits = toGramBitSet(dict, corpusWf);
			if (wordStr.length() > suffixLength) {
				String suffix = getSuffix(wordStr);
				String suffixKey = makeSuffixKey(suffix);
				wsBuilder.increment(suffixKey, corpusWfBits);
			} else {
				// if word length is equal or less than suffixLength
				// then memorize the whole word
				wsBuilder.increment(wordStr, corpusWfBits);
				shortWordsExamined++;
			}
			wordsExamined++;
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		try {
			WordformStore ws = wsBuilder.build();
			ws.setProperty(KEY_SUFFIX_LENGTH, suffixLength);
			ws.persist(wsFile);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		getLogger().info(String.format(
				"Words examined: %s. Words shorter than the suffix length (%s): %s",
				wordsExamined, suffixLength, shortWordsExamined));
	}
}