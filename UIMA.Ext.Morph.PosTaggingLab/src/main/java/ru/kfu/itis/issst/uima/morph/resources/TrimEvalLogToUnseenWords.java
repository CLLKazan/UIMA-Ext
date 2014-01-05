/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.resources;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static ru.kfu.itis.cll.uima.io.IoUtils.openPrintWriter;
import static ru.kfu.itis.cll.uima.io.IoUtils.openReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * TODO This is a temporary quick solution to calculate a tagger accuracy on
 * unseen words
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TrimEvalLogToUnseenWords {

	public static void main(String[] args) throws Exception {
		TrimEvalLogToUnseenWords launcher = new TrimEvalLogToUnseenWords();
		new JCommander(launcher, args);
		launcher.run();
	}

	private Logger log = LoggerFactory.getLogger(getClass());

	@Parameter(names = "--unseen-words", required = true)
	private File unseenWordInfoFile;
	@Parameter(names = "--eval-log", required = true)
	private File srcEvalLogFile;

	private TrimEvalLogToUnseenWords() {
	}

	private void run() throws Exception {
		List<String> unseenWordsList = TokenInfoWriter.parseFileToTokens(unseenWordInfoFile);
		Multiset<String> unseenWordsBag = HashMultiset.create(unseenWordsList);
		Set<String> unseenWords = newHashSet(unseenWordsList);
		log.info("Finished reading {}. Unique words: {}", unseenWordInfoFile, unseenWords.size());
		//
		File dir = srcEvalLogFile.getParentFile();
		File resultEvalLogFile;
		{
			if (dir == null) {
				dir = new File(".");
			}
			String baseName = FilenameUtils.getBaseName(srcEvalLogFile.getName());
			String extension = FilenameUtils.getExtension(srcEvalLogFile.getName());
			String resultName = baseName + "-unseen" + "." + extension;
			resultEvalLogFile = new File(dir, resultName);
		}
		//
		BufferedReader in = openReader(srcEvalLogFile);
		PrintWriter out = openPrintWriter(resultEvalLogFile);
		int matchedNum = 0;
		int missedNum = 0;
		try {
			String line;
			int lineIndex = 0;
			while ((line = in.readLine()) != null) {
				lineIndex++;
				if (line.isEmpty()) {
					continue;
				}
				// EXAMPLE line:
				// Word	Exact	Календарь|wordforms={[grammems={NOUN,masc,sing,nomn}]}	0	Календарь|wordforms={[grammems={NOUN,masc,sing,nomn}]}	0	00067797.xhtml
				ArrayList<String> tsValues = newArrayList(tabSplitter.split(line));
				if (tsValues.size() != 7) {
					throw new IllegalStateException();
				}
				String matchStatus = tsValues.get(1);
				String goldAnnoDesc = tsValues.get(2);
				// ignore Spurious - the evaluation is based on the corpus tokenization
				if ("Spurious".equals(matchStatus)) {
					continue;
				}
				int txtSepIndex = goldAnnoDesc.indexOf("|wordforms");
				if (txtSepIndex < 0) {
					throw new IllegalStateException(line);
				}
				String token = goldAnnoDesc.substring(0, txtSepIndex);
				if (unseenWords.contains(token)) {
					out.println(line);
					// for example, this flag is false for 'Partial' records
					boolean isEssentialRecord = false;
					if ("Missing".equals(matchStatus)) {
						missedNum++;
						isEssentialRecord = true;
					} else if ("Exact".equals(matchStatus)) {
						matchedNum++;
						isEssentialRecord = true;
					}
					if (isEssentialRecord && !unseenWordsBag.remove(token)) {
						throw new IllegalStateException(String.format(
								"Unexpected word at line %s:\n%s", lineIndex, line));
					}
				}
			}
		} finally {
			closeQuietly(in);
			closeQuietly(out);
		}
		double accuracy = (double) matchedNum / (matchedNum + missedNum);
		FileUtils.writeLines(new File(dir, "strict-eval-results-unseen.txt"), Arrays.asList(
				format("Matched\t%s", matchedNum),
				format("Missed\t%s", missedNum),
				format("Accuracy\t%.2f%%", accuracy * 100)));
	}

	private static final Splitter tabSplitter = Splitter.on('\t');
}
