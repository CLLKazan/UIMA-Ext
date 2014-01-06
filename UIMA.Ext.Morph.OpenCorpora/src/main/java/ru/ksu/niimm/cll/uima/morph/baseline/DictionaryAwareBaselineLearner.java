/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.normalizeToDictionary;
import static ru.ksu.niimm.cll.uima.morph.baseline.PUtils.toGramBitSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.itis.cll.uima.cas.FSUtils;

import com.google.common.base.Joiner;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(multipleDeploymentAllowed = false)
public class DictionaryAwareBaselineLearner extends BaselineAnnotator {

	public static final String PARAM_MODEL_OUTPUT_FILE = "modelOutputFile";

	// config fields
	@ConfigurationParameter(name = PARAM_MODEL_OUTPUT_FILE, mandatory = true)
	private File modelOutputFile;
	// derived
	private File modelDir;
	// state fields
	private WordformStoreBuilder wfStoreBuilder;
	private PrintWriter unknownWordsOut;
	private PrintWriter dictNotCompliantOut;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		modelDir = modelOutputFile.getParentFile();
		if (modelDir == null) {
			// fallback to current directory
			modelDir = new File(".");
		}
		//
		try {
			File unknownWordsFile = new File(modelDir, "unknown-words.txt");
			unknownWordsOut = makePrintWriter(unknownWordsFile);
			File dictNotCompliantFile = new File(modelDir, "dict-non-compliant-words.txt");
			dictNotCompliantOut = makePrintWriter(dictNotCompliantFile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
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
			if (word.getToken() instanceof NUM) {
				continue;
			}
			Collection<org.opencorpora.cas.Wordform> corpusWfs = FSCollectionFactory.create(
					word.getWordforms(),
					org.opencorpora.cas.Wordform.class);
			if (corpusWfs.isEmpty()) {
				continue;
			}
			if (corpusWfs.size() > 1) {
				getLogger().warn(String.format("Too much wordforms for word %s in %s",
						toPrettyString(word), getDocumentUri(jcas)));
			}
			org.opencorpora.cas.Wordform corpusWf = corpusWfs.iterator().next();
			//
			String wordString = normalizeToDictionary(word.getCoveredText());
			Set<BitSet> dictEntries = trimAndMergePosBits(dict.getEntries(wordString));
			if (dictEntries == null || dictEntries.isEmpty()) {
				reportUnknownWord(wordString);
			} else if (dictEntries.size() == 1) {
				// there is no ambiguity
			} else {
				// GBS ~ Grammeme BitSet
				BitSet corpusWfGBS = toGramBitSet(dict, corpusWf);
				if (dictEntries.contains(corpusWfGBS)) {
					wfStoreBuilder.increment(wordString, corpusWfGBS);
				} else {
					reportNotDictionaryCompliant(wordString, corpusWf);
				}
			}
		}
	}

	/*
	private boolean isDictionaryCompliant(Set<BitSet> dictEntries, BitSet _corpusWfGBS) {
		for (BitSet deBits : dictEntries) {
			BitSet corpusWfGBS = (BitSet) _corpusWfGBS.clone();
			corpusWfGBS.andNot(deBits);
			if (corpusWfGBS.cardinality() == 0) {
				return true;
			}
		}
		return false;
	}
	*/

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		// 
		IOUtils.closeQuietly(unknownWordsOut);
		IOUtils.closeQuietly(dictNotCompliantOut);
		try {
			WordformStore ws = wfStoreBuilder.build();
			ws.persist(modelOutputFile);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private void reportUnknownWord(String wordStr) {
		unknownWordsOut.println(wordStr);
	}

	private static final Joiner gramJoiner = Joiner.on(',');

	private void reportNotDictionaryCompliant(String wordStr, org.opencorpora.cas.Wordform corpusWf) {
		List<String> corpusWfGrams = FSUtils.toList(corpusWf.getGrammems());
		StringBuilder sb = new StringBuilder();
		sb.append(wordStr).append('\t');
		gramJoiner.appendTo(sb, corpusWfGrams);
		dictNotCompliantOut.println(sb);
	}

	@Override
	public void destroy() {
		IOUtils.closeQuietly(unknownWordsOut);
		IOUtils.closeQuietly(dictNotCompliantOut);
		super.destroy();
	}

	private static PrintWriter makePrintWriter(File outFile) throws IOException {
		FileOutputStream fos = new FileOutputStream(outFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		return new PrintWriter(new BufferedWriter(osw), true);
	}
}