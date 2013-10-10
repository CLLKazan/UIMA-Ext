/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.normalizeToDictionary;
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
		wsBuilder = new DefaultWordformStoreBuilder();
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Word word : JCasUtil.select(jCas, Word.class)) {
			// check corpus word sanity
			if (word.getWordforms() == null) {
				continue;
			}
			Collection<Wordform> corpusWfs = FSCollectionFactory.create(
					word.getWordforms(),
					Wordform.class);
			if (corpusWfs.isEmpty()) {
				continue;
			}
			if (corpusWfs.size() > 1) {
				getLogger().warn(String.format("Too much wordforms for word %s in %s",
						toPrettyString(word), getDocumentUri(jCas)));
			}
			Wordform corpusWf = corpusWfs.iterator().next();
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
			wsBuilder.persist(wsFile);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		getLogger().info(String.format(
				"Words examined: %s. Words shorter than the suffix length (%s): %s",
				wordsExamined, suffixLength, shortWordsExamined));
	}
}