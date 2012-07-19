/**
 * 
 */
package ru.kfu.itis.cll.uima.dictpatterns.core;

import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictPatternMatcherTest {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage: <dictionaryFile> <testFile-one input per line>");
			return;
		}
		File dictFile = new File(args[0]);
		if (!dictFile.isFile()) {
			System.err.println("Dictionary file is not exist");
			return;
		}
		File testFile = new File(args[1]);
		if (!testFile.isFile()) {
			System.err.println("Test file is not exist");
			return;
		}
		List<String> inputsList = FileUtils.readLines(testFile, "utf-8");

		long beforeInit = currentTimeMillis();
		DictPatternsMatcher matcher = new DictPatternsMatcher(dictFile.toURI().toURL(), "utf-8");
		print("Initialized in %s ms", currentTimeMillis() - beforeInit);
		long matchAggrTime = 0;
		for (String input : inputsList) {
			long beforeMatch = currentTimeMillis();
			List<DictPatternMatch> matches = matcher.match(input);
			matchAggrTime += currentTimeMillis() - beforeMatch;
			for (DictPatternMatch match : matches) {
				print(match, input);
				print("================================");
			}
		}
		print("Average time per input: %.2f ms", matchAggrTime / (float) inputsList.size());
	}

	private static void print(DictPatternMatch match, String input) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			sb.append(' ');
		}
		for (PatternElementSpan elem : match.getMatchSpans()) {
			String elemStr = elem.getPatternElement();
			int elemLength = elem.getEnd() - elem.getBegin();
			if (elemStr.length() < elemLength) {
				elemStr = StringUtils.rightPad(elemStr, elemLength);
			} else {
				elemStr.substring(0, elemLength);
			}
			sb.replace(elem.getBegin(), elem.getEnd(), elemStr);
		}
		System.out.println(input);
		System.out.println(sb.toString());
		print("PatternId = %s", match.getPatternId());
	}

	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}
}