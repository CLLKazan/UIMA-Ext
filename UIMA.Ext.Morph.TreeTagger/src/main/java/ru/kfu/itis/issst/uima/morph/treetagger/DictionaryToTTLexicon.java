/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LemmaByGrammemFilter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LemmaPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.XmlDictionaryParser;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.YoLemmaPostProcessor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryToTTLexicon {

	public static final String LEXICON_FILENAME = "lexicon-src.txt";
	public static final String OPEN_CLASS_TAGS_FILENAME = "open-class-tags.txt";
	public static final String CLOSED_CLASS_TAGS_FILENAME = "closed-class-tags.txt";

	public static void main(String[] args) throws Exception {
		DictionaryToTTLexicon launcher = new DictionaryToTTLexicon();
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
	private DictionaryBasedTagMapper tagMapper;
	private PosTrimmer posTrimmer;
	private BitSet closedClassTagsMask;
	// state fields
	private PrintWriter lexiconOut;
	private TreeSet<String> openClassTags;
	private TreeSet<String> closedClassTags;

	private DictionaryToTTLexicon() {
	}

	private void run() throws Exception {
		FileInputStream xmlDictIS = openInputStream(xmlDictFile);
		FileOutputStream os = openOutputStream(new File(outputDir, LEXICON_FILENAME));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
		lexiconOut = new PrintWriter(bw);
		openClassTags = Sets.newTreeSet();
		closedClassTags = Sets.newTreeSet();
		try {
			XmlDictionaryParser.parse(xmlDictIS,
					new InjectDictionary(),
					new LemmaByGrammemFilter("Surn", "Patr", "Orgn"),
					YoLemmaPostProcessor.INSTANCE,
					new WriteTTLexEntryLemmaPostProcessor());
		} finally {
			closeQuietly(xmlDictIS);
			closeQuietly(lexiconOut);
		}
		// output tag files
		writeLines(new File(outputDir, OPEN_CLASS_TAGS_FILENAME), "utf-8", openClassTags);
		writeLines(new File(outputDir, CLOSED_CLASS_TAGS_FILENAME), "utf-8", closedClassTags);
	}

	private class WriteTTLexEntryLemmaPostProcessor implements LemmaPostProcessor {
		@Override
		public boolean process(MorphDictionary dict, Lemma lemmaObj,
				Multimap<String, Wordform> wfMap) {
			String lemma = lemmaObj.getString();
			for (String wfStr : wfMap.keySet()) {
				lexiconOut.print(wfStr);
				// collect uniq tags
				Set<String> tags = Sets.newHashSet();
				for (Wordform wf : wfMap.get(wfStr)) {
					BitSet wfBits = wf.getGrammems();
					wfBits.or(lemmaObj.getGrammems());
					posTrimmer.trimInPlace(wfBits);
					boolean closedClassTag = isClosedClasTag(wfBits);
					String tag = tagMapper.toTag(wfBits);
					tags.add(tag);
					if (closedClassTag) {
						closedClassTags.add(tag);
					} else {
						openClassTags.add(tag);
					}
				}
				for (String tag : tags) {
					lexiconOut.print('\t');
					// print tag
					lexiconOut.print(tag);
					// print lemma
					lexiconOut.print(' ');
					lexiconOut.print(lemma);
				}
				lexiconOut.println();
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
			if (closedClassTagsMask == null) {
				closedClassTagsMask = new BitSet();
				for (String cpGram : closedPosSet) {
					closedClassTagsMask.set(dict.getGrammemNumId(cpGram));
				}
			}
			return true;
		}
	}

	private boolean isClosedClasTag(final BitSet _wfBits) {
		BitSet wfBits = (BitSet) _wfBits.clone();
		wfBits.and(closedClassTagsMask);
		return !wfBits.isEmpty();
	}

	private static final Set<String> closedPosSet = ImmutableSet.of(NPRO, PREP, CONJ, PRCL);
}
