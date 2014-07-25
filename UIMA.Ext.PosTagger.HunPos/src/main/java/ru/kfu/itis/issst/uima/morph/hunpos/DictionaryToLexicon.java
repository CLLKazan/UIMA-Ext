/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import ru.kfu.itis.issst.uima.morph.commons.GramModelBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.model.Lemma;
import ru.kfu.itis.issst.uima.morph.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryImpl;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryListenerBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.XmlDictionaryParser;
import ru.ksu.niimm.cll.uima.morph.ruscorpora.RNCDictionaryExtension;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryToLexicon {

	public static final String LEXICON_FILENAME = "lexicon.txt";

	public static void main(String[] args) throws Exception {
		DictionaryToLexicon launcher = new DictionaryToLexicon();
		new JCommander(launcher, args);
		launcher.run();
	}

	// config fields
	@Parameter(names = "-d", required = true)
	private File xmlDictFile;
	@Parameter(names = { "-o", "--output-dir" }, required = true)
	private File outputDir;
	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> posCategoriesList;
	//
	private GramModelBasedTagMapper tagMapper;
	private PosTrimmer posTrimmer;
	// state fields
	private Multimap<String, String> lexiconMM;

	private DictionaryToLexicon() {
	}

	public DictionaryToLexicon(File xmlDictFile, File outputDir,
			Collection<String> posCategories) {
		this();
		this.xmlDictFile = xmlDictFile;
		this.outputDir = outputDir;
		this.posCategoriesList = ImmutableList.copyOf(posCategories);
	}

	public void run() throws Exception {
		MorphDictionaryImpl dict = new MorphDictionaryImpl();
		dict.addListener(new GramModelListener());
		dict.addListener(new WriteLexEntryListener());
		FileInputStream xmlDictIS = openInputStream(xmlDictFile);
		lexiconMM = TreeMultimap.create();
		// parse dictionary xml
		try {
			XmlDictionaryParser.parse(dict,
					xmlDictIS,
					// FIXME
					new RNCDictionaryExtension());
		} finally {
			closeQuietly(xmlDictIS);
		}
		// output lexiconMM
		File lexiconFile = new File(outputDir, LEXICON_FILENAME);
		writeLexicon(lexiconMM, lexiconFile);
	}

	private class WriteLexEntryListener extends MorphDictionaryListenerBase {
		@Override
		public void onWordformAdded(MorphDictionary dict, String wfString, Wordform wf) {
			Lemma lemmaObj = dict.getLemma(wf.getLemmaId());
			// collect uniq tags
			BitSet wfBits = wf.getGrammems();
			wfBits.or(lemmaObj.getGrammems());
			posTrimmer.trimInPlace(wfBits);
			String tag = tagMapper.toTag(wfBits);
			tag = tag.intern();
			lexiconMM.put(wfString, tag);
		}
	}

	private class GramModelListener extends MorphDictionaryListenerBase {
		@Override
		public void onGramModelSet(MorphDictionary dict) {
			GramModel gm = dict.getGramModel();
			posTrimmer = new PosTrimmer(gm, Sets.newHashSet(posCategoriesList));
			tagMapper = new GramModelBasedTagMapper(gm);
		}
	}

	private static void writeLexicon(Multimap<String, String> lexiconMM, File outputFile)
			throws IOException {
		FileOutputStream os = openOutputStream(outputFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
		PrintWriter lexiconOut = new PrintWriter(bw);
		try {
			for (String wf : lexiconMM.keySet()) {
				writeLexiconEntry(lexiconOut, wf, lexiconMM.get(wf));
			}
		} finally {
			closeQuietly(lexiconOut);
		}
	}

	private static void writeLexiconEntry(PrintWriter lexiconOut, String wf, Iterable<String> wfTags) {
		lexiconOut.print(wf);
		for (String tag : wfTags) {
			lexiconOut.print('\t');
			lexiconOut.print(tag);
		}
		lexiconOut.println();
	}
}
