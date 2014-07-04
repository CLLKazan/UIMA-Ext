/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static org.apache.commons.io.FileUtils.openInputStream;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.OTHER_PUNCTUATION_TAG;
import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.punctuationTagMap;

import java.io.File;
import java.io.FileInputStream;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ru.kfu.itis.issst.uima.morph.commons.DictionaryBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TagUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.PosTrimmer;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LexemePostProcessorBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.XmlDictionaryParser;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.YoLemmaPostProcessor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryToTTLexicon {

	public static final String LEXICON_FILENAME = "lexicon.txt";
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
	//
	private DictionaryBasedTagMapper tagMapper;
	private PosTrimmer posTrimmer;
	private Function<BitSet, Boolean> closedClassTagIndicator;
	// state fields
	private Multimap<String, String> lexiconMM;
	private TreeSet<String> openClassTags;
	private TreeSet<String> closedClassTags;

	private DictionaryToTTLexicon() {
	}

	public DictionaryToTTLexicon(File xmlDictFile, File outputDir,
			Collection<String> posCategories) {
		this();
		this.xmlDictFile = xmlDictFile;
		this.outputDir = outputDir;
		this.posCategoriesList = ImmutableList.copyOf(posCategories);
	}

	public void run() throws Exception {
		FileInputStream xmlDictIS = openInputStream(xmlDictFile);
		openClassTags = Sets.newTreeSet();
		openClassTags.add(OTHER_PUNCTUATION_TAG);
		closedClassTags = Sets.newTreeSet();
		closedClassTags.addAll(punctuationTagMap.values());
		lexiconMM = TreeMultimap.create();
		// add sentence end tags
		lexiconMM.put(".", "SENT");
		lexiconMM.put("!", "SENT");
		lexiconMM.put("?", "SENT");
		// HACK - add example of OTHER_PUNCTUATION_TAG to avoid training procedure failures
		lexiconMM.put("@", OTHER_PUNCTUATION_TAG);
		// add punctuation tags
		lexiconMM.putAll(Multimaps.forMap(punctuationTagMap));
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
		LexiconWriter.write(lexiconMM, lexiconFile);
		// output tag files
		writeLines(new File(outputDir, OPEN_CLASS_TAGS_FILENAME), "utf-8", openClassTags);
		writeLines(new File(outputDir, CLOSED_CLASS_TAGS_FILENAME), "utf-8", closedClassTags);
	}

	private class WriteTTLexEntryLemmaPostProcessor extends LexemePostProcessorBase {
		@Override
		public boolean process(MorphDictionary dict, Lemma.Builder lemmaObj,
				Multimap<String, Wordform> wfMap) {
			for (String wfStr : wfMap.keySet()) {
				// collect uniq tags
				Set<String> tags = Sets.newHashSet();
				for (Wordform wf : wfMap.get(wfStr)) {
					BitSet wfBits = wf.getGrammems();
					wfBits.or(lemmaObj.getGrammems());
					posTrimmer.trimInPlace(wfBits);
					boolean closedClassTag = closedClassTagIndicator.apply(wfBits);
					String tag = tagMapper.toTag(wfBits);
					tag = tag.intern();
					tags.add(tag);
					if (closedClassTag) {
						closedClassTags.add(tag);
					} else {
						openClassTags.add(tag);
					}
				}
				lexiconMM.putAll(wfStr, tags);
			}
			// return false because we are not interested in a dictionary instance
			return false;
		}
	}

	// TODO:LOW this looks like a hack but should work well for experimental purposes
	private class InjectDictionary extends LexemePostProcessorBase {
		@Override
		public boolean process(MorphDictionary dict, Lemma.Builder lemma,
				Multimap<String, Wordform> wfMap) {
			// initialize posTrimmer at the first time
			// then always return true and do nothing
			GramModel gm = dict.getGramModel();
			if (posTrimmer == null) {
				posTrimmer = new PosTrimmer(gm, Sets.newHashSet(posCategoriesList));
			}
			if (tagMapper == null) {
				tagMapper = new DictionaryBasedTagMapper(gm);
			}
			if (closedClassTagIndicator == null) {
				closedClassTagIndicator = TagUtils.getClosedClassIndicator(gm);
			}
			return true;
		}
	}
}
