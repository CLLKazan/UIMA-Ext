/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils.toGramBitSet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.BitSet;
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
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;

import com.google.common.base.Joiner;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(multipleDeploymentAllowed = false)
public class DictionaryAwareBaselineLearner extends DictionaryAwareBaselineAnnotator {

	public static final String PARAM_MODEL_OUTPUT_FILE = "modelOutputFile";

	// config fields
	@ConfigurationParameter(name = PARAM_MODEL_OUTPUT_FILE, mandatory = true)
	private File modelOutputFile;
	@ConfigurationParameter(name = PARAM_TARGET_POS_CATEGORIES, mandatory = true)
	private String[] targetPosCategories;
	// derived
	private File modelDir;
	// state fields
	private WordformStoreBuilder<BitSet> wfStoreBuilder;
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
		posTrimmer = new PosTrimmer(gramModel, targetPosCategories);
		//
		wfStoreBuilder = new DefaultWordformStoreBuilder<BitSet>();
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		for (Word word : JCasUtil.select(jcas, Word.class)) {
			if (word.getToken() instanceof NUM) {
				continue;
			}
			// check corpus word sanity
			org.opencorpora.cas.Wordform corpusWf = MorphCasUtils.getOnlyWordform(word);
			if (corpusWf == null) {
				continue;
			}
			//
			String wordString = WordUtils.normalizeToDictionaryForm(word.getCoveredText());
			Set<BitSet> dictEntries = trimAndMergePosBits(dict.getEntries(wordString));
			//
			BitSet corpusWfGBS = toGramBitSet(gramModel, corpusWf);
			//
			if (dictEntries == null || dictEntries.isEmpty()) {
				reportUnknownWord(wordString);
			} else if (dictEntries.size() == 1) {
				// there is no ambiguity
				// but just check for compliance
				if (!dictEntries.contains(corpusWfGBS)) {
					reportNotDictionaryCompliant(wordString, corpusWf);
				}
			} else {
				// GBS ~ Grammeme BitSet
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
			WordformStore<BitSet> ws = wfStoreBuilder.build();
			ws.setProperty(PARAM_TARGET_POS_CATEGORIES, targetPosCategories);
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