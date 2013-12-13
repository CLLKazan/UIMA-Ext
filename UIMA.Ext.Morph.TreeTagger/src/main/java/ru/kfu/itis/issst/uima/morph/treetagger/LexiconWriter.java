/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.kfu.itis.cll.uima.io.IoUtils.openPrintWriter;
import static ru.kfu.itis.cll.uima.io.IoUtils.openReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class LexiconWriter {

	private static final Logger log = LoggerFactory.getLogger(LexiconWriter.class);

	public static void write(Multimap<String, String> lexiconMM, File outputFile)
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
			lexiconOut.print(' ');
			// print dummy lemma
			lexiconOut.print("-");
		}
		lexiconOut.println();
	}

	/**
	 * @param biggerLexFile
	 *            must be sorted according to String natural ordering
	 * @param smallerLexFile
	 *            must be sorted according to String natural ordering
	 * @param outputFile
	 * @throws IOException
	 */
	public static void mergeLexicons(File biggerLexFile, File smallerLexFile, File outputFile)
			throws IOException {
		log.info("Starting to merge lexicons (1) {} and (2) {} into {}", new Object[] {
				biggerLexFile, smallerLexFile, outputFile
		});
		LineIterator bigIter = new LineIterator(openReader(biggerLexFile));
		LineIterator smallIter = new LineIterator(openReader(smallerLexFile));
		PrintWriter outWriter = openPrintWriter(outputFile);
		//
		int mergedCounter = 0;
		try {
			Iterator<String> mergingIterator = Iterators.mergeSorted(
					asList(bigIter, smallIter), new NaturalComparator<String>());
			String lastWordform = null;
			Set<String> lastWordformTags = Sets.newHashSet();
			while (mergingIterator.hasNext()) {
				String line = mergingIterator.next();
				LexiconEntry lexEntry = parseEntry(line);
				if (!lexEntry.wordform.equals(lastWordform)) {
					if (!lastWordformTags.isEmpty()) {
						writeLexiconEntry(outWriter, lastWordform, lastWordformTags);
					}
					lastWordform = lexEntry.wordform;
					lastWordformTags = Sets.newHashSet(lexEntry.tags);
				} else {
					lastWordformTags.addAll(lexEntry.tags);
					mergedCounter++;
				}
			}
			// write last entry
			if (!lastWordformTags.isEmpty()) {
				writeLexiconEntry(outWriter, lastWordform, lastWordformTags);
			}
		} finally {
			bigIter.close();
			smallIter.close();
			closeQuietly(outWriter);
		}
		log.info("Finished. {} entries have been merged.", mergedCounter);
	}

	public static void appendLexiconEntry(File lexiconFile, String wordform, String tag)
			throws IOException {
		PrintWriter lexWriter = openPrintWriter(lexiconFile, true);
		try {
			writeLexiconEntry(lexWriter, wordform, asList(tag));
		} finally {
			closeQuietly(lexWriter);
		}
	}

	public static Iterator<LexiconEntry> toIterator(final BufferedReader reader) {
		return new AbstractIterator<LexiconEntry>() {
			@Override
			protected LexiconEntry computeNext() {
				String line;
				try {
					line = reader.readLine();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
				if (line == null) {
					return endOfData();
				}
				return parseEntry(line);
			}
		};
	}

	private static LexiconEntry parseEntry(String line) {
		Iterator<String> iter = entryTagsSplitter.split(line).iterator();
		String wordform = iter.next();
		if (!iter.hasNext()) {
			throw illegalLexiconLine(line);
		}
		Set<String> tags = Sets.newLinkedHashSet();
		while (iter.hasNext()) {
			String[] tagLemmaTuple = iter.next().split(" ");
			if (tagLemmaTuple.length != 2) {
				throw illegalLexiconLine(line);
			}
			tags.add(tagLemmaTuple[0]);
		}
		return new LexiconEntry(wordform, tags);
	}

	private static RuntimeException illegalLexiconLine(String line) {
		return new IllegalStateException(String.format(
				"Illegal lexicon line:\n%s", line));
	}

	private static final Splitter entryTagsSplitter = Splitter.on('\t');

	private LexiconWriter() {
	}

	private static class NaturalComparator<T extends Comparable<? super T>>
			implements Comparator<T> {
		@Override
		public int compare(T o1, T o2) {
			return o1.compareTo(o2);
		}
	}

	public static class LexiconEntry {
		final String wordform;
		final Set<String> tags;

		public LexiconEntry(String wordform, Set<String> tags) {
			this.wordform = wordform;
			this.tags = tags;
		}
	}
}
