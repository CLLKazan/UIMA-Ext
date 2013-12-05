/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

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
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryToTTLexicon {

	public static void main(String[] args) throws Exception {
		DictionaryToTTLexicon launcher = new DictionaryToTTLexicon();
		new JCommander(launcher, args);
		launcher.run();
	}

	// config fields
	@Parameter(names = "-d", required = true)
	private File xmlDictFile;
	@Parameter(names = "-o", required = true)
	private File outputFile;
	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> posCategoriesList;
	private DictionaryBasedTagMapper tagMapper;
	private PosTrimmer posTrimmer;
	// state fields
	private PrintWriter out;

	private DictionaryToTTLexicon() {
	}

	private void run() throws Exception {
		FileInputStream xmlDictIS = openInputStream(xmlDictFile);
		FileOutputStream os = openOutputStream(outputFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
		out = new PrintWriter(bw);
		try {
			XmlDictionaryParser.parse(xmlDictIS,
					new InjectDictionary(),
					new LemmaByGrammemFilter("Surn", "Patr", "Orgn"),
					YoLemmaPostProcessor.INSTANCE,
					new WriteTTLexEntryLemmaPostProcessor());
		} finally {
			closeQuietly(xmlDictIS);
			closeQuietly(out);
		}
	}

	private class WriteTTLexEntryLemmaPostProcessor implements LemmaPostProcessor {
		@Override
		public boolean process(MorphDictionary dict, Lemma lemmaObj,
				Multimap<String, Wordform> wfMap) {
			String lemma = lemmaObj.getString();
			for (String wfStr : wfMap.keySet()) {
				out.print(wfStr);
				// collect uniq tags
				Set<String> tags = Sets.newHashSet();
				for (Wordform wf : wfMap.get(wfStr)) {
					BitSet wfBits = wf.getGrammems();
					wfBits.or(lemmaObj.getGrammems());
					posTrimmer.trimInPlace(wfBits);
					tags.add(tagMapper.toTag(wfBits));
				}
				for (String tag : tags) {
					out.print('\t');
					// print tag
					out.print(tag);
					// print lemma
					out.print(' ');
					out.print(lemma);
				}
				out.println();
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
}
