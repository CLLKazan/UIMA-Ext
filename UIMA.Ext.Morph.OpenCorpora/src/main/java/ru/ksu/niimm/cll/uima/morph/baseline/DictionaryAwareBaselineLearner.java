/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.descriptor.OperationalProperties;
import org.uimafit.util.FSCollectionFactory;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Joiner;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
@OperationalProperties(multipleDeploymentAllowed = false)
public class DictionaryAwareBaselineLearner extends JCasAnnotator_ImplBase {

	public static final String RESOURCE_MORPH_DICTIONARY = "MorphDictionary";
	public static final String PARAM_MODEL_OUTPUT_FILE = "modelOutputFile";

	@ExternalResource(key = RESOURCE_MORPH_DICTIONARY)
	private MorphDictionaryHolder dictHolder;
	@ConfigurationParameter(name = PARAM_MODEL_OUTPUT_FILE, mandatory = true)
	private File modelOutputFile;
	// derived
	private MorphDictionary dict;
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
		dict = dictHolder.getDictionary();
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
			String wordString = word.getCoveredText();
			wordString = WordUtils.normalizeToDictionaryForm(wordString);
			List<Wordform> dictEntries = dict.getEntries(wordString);
			if (word.getWordforms() == null) {
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
			if (dictEntries == null) {
				reportUnknownWord(wordString);
			} else if (dictEntries.size() == 1) {
				// there is no ambiguity
			} else {
				// GBS ~ Grammeme BitSet
				BitSet corpusWfGBS = toGramBitSet(corpusWf);
				if (isDictionaryCompliant(dictEntries, corpusWfGBS)) {
					wfStoreBuilder.increment(wordString, corpusWfGBS);
				} else {
					reportNotDictionaryCompliant(wordString, corpusWf);
				}
			}
		}
	}

	private boolean isDictionaryCompliant(List<Wordform> dictEntries, BitSet _corpusWfGBS) {
		for (Wordform de : dictEntries) {
			BitSet corpusWfGBS = (BitSet) _corpusWfGBS.clone();
			BitSet dictGBS = toGramBitSet(de);
			corpusWfGBS.andNot(dictGBS);
			if (corpusWfGBS.cardinality() == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		// 
		IOUtils.closeQuietly(unknownWordsOut);
		IOUtils.closeQuietly(dictNotCompliantOut);
		try {
			wfStoreBuilder.persist(modelOutputFile);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private BitSet toGramBitSet(Wordform wf) {
		BitSet bs = wf.getGrammems();
		bs.or(dict.getLemma(wf.getLemmaId()).getGrammems());
		return bs;
	}

	private BitSet toGramBitSet(org.opencorpora.cas.Wordform casWf) {
		BitSet result = new BitSet(dict.getGrammemMaxNumId());
		List<String> casWfGrams = FSUtils.toList(casWf.getGrammems());
		for (String gr : casWfGrams) {
			result.set(dict.getGrammemNumId(gr));
		}
		// TODO-MEMORY OPTIMIZATION- cache BitSets instances
		return result;
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