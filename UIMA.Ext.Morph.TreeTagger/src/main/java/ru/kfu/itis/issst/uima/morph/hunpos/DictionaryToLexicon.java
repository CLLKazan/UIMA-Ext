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
import java.util.Set;

import ru.kfu.itis.issst.uima.morph.commons.DictionaryBasedTagMapper;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LemmaPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.XmlDictionaryParser;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.YoLemmaPostProcessor;

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
	private DictionaryBasedTagMapper tagMapper;
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
		FileInputStream xmlDictIS = openInputStream(xmlDictFile);
		lexiconMM = TreeMultimap.create();
		// parse dictionary xml
		try {
			XmlDictionaryParser.parse(xmlDictIS,
					new InjectDictionary(),
					YoLemmaPostProcessor.INSTANCE,
					new WriteTTLexEntryLemmaPostProcessor());
		} finally {
			closeQuietly(xmlDictIS);
		}
		// output lexiconMM
		File lexiconFile = new File(outputDir, LEXICON_FILENAME);
		writeLexicon(lexiconMM, lexiconFile);
	}

	private class WriteTTLexEntryLemmaPostProcessor implements LemmaPostProcessor {
		@Override
		public boolean process(MorphDictionary dict, Lemma lemmaObj,
				Multimap<String, Wordform> wfMap) {
			for (String wfStr : wfMap.keySet()) {
				// collect uniq tags
				Set<String> tags = Sets.newHashSet();
				for (Wordform wf : wfMap.get(wfStr)) {
					BitSet wfBits = wf.getGrammems();
					wfBits.or(lemmaObj.getGrammems());
					posTrimmer.trimInPlace(wfBits);
					String tag = tagMapper.toTag(wfBits);
					tag = tag.intern();
					tags.add(tag);
				}
				lexiconMM.putAll(wfStr, tags);
			}
			// return false because we are not interested in a dictionary instance
			return false;
		}
	}

	// TODO:LOW this looks like a hack but should work well for experimental purposes
	private class InjectDictionary implements LemmaPostProcessor {
		@Override
		public boolean process(MorphDictionary dict, Lemma lemma, Multimap<String, Wordform> wfMap) {
			// initialize posTrimmer at the first time
			// then always return true and do nothing
			if (posTrimmer == null) {
				posTrimmer = new PosTrimmer(dict, Sets.newHashSet(posCategoriesList));
			}
			if (tagMapper == null) {
				tagMapper = new DictionaryBasedTagMapper(dict);
			}
			return true;
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
