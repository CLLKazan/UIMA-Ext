/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static ru.kfu.itis.issst.uima.morph.model.Wordform.getAllGramBits;

import java.io.File;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.WordUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.model.Wordform;
import ru.kfu.itis.issst.util.cli.FileValueValidator;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DictionaryDeserializer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphologyDictionaryLookup {

	public static void main(String[] args) throws Exception {
		MorphologyDictionaryLookup launcher = new MorphologyDictionaryLookup();
		new JCommander(launcher, args);
		launcher.run();
	}

	// config fields
	@Parameter(required = true, validateValueWith = FileValueValidator.class)
	private List<File> srcFiles;
	@Parameter(names = { "-d", "--dict-file" }, required = true)
	private File serializedDictFile;
	// state fields
	private PrintWriter out;

	private void run() throws Exception {
		// read dictionary
		MorphDictionary dict = DictionaryDeserializer.from(serializedDictFile);
		for (File srcFile : srcFiles) {
			// read input
			List<String> srcLines = FileUtils.readLines(srcFile, "utf-8");
			// prepare output
			File outFile = getOutFile(srcFile);
			out = IoUtils.openPrintWriter(outFile);

			try {
				for (String s : srcLines) {
					s = s.trim();
					if (s.isEmpty()) {
						continue;
					}
					s = WordUtils.normalizeToDictionaryForm(s);
					List<Wordform> sEntries = dict.getEntries(s);
					if (sEntries == null || sEntries.isEmpty()) {
						writeEntry(s, "?UNKNOWN?");
						continue;
					}
					for (Wordform se : sEntries) {
						BitSet gramBits = getAllGramBits(se, dict);
						List<String> grams = dict.getGramModel().toGramSet(gramBits);
						writeEntry(s, grams);
					}
				}
			} finally {
				IOUtils.closeQuietly(out);
			}
		}
	}

	private void writeEntry(String src, String gram) {
		writeEntry(src, ImmutableList.of(gram));
	}

	private void writeEntry(String src, Iterable<String> grams) {
		out.println(String.format("%s\t%s",
				src, gramJoiner.join(grams)));
	}

	private static final Joiner gramJoiner = Joiner.on(',');

	private File getOutFile(File srcFile) {
		File dir = srcFile.getParentFile();
		if (dir == null) {
			dir = new File(".");
		}
		String baseName = FilenameUtils.getBaseName(srcFile.getName());
		return new File(dir, baseName + ".out");
	}
}
