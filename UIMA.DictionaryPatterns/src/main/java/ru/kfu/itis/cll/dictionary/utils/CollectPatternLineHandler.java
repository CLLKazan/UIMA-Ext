/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CollectPatternLineHandler implements LineHandler {

	public static final String WILDCARD = "%%%";

	private File seedNamesFile;
	private int expectedSeenNamesNum;
	private TokenizerImpl tokenizer;
	private PrintWriter writer;
	// derived
	private Set<String> seedNames;

	public CollectPatternLineHandler(File seedNamesFile, int expectedSeenNamesNum,
			File outputFile)
			throws IOException {
		this.seedNamesFile = seedNamesFile;
		this.expectedSeenNamesNum = expectedSeenNamesNum;
		tokenizer = new TokenizerImpl(true);
		writer = Utils.writer(outputFile);
		initSeedNames();
	}

	private void initSeedNames() throws IOException {
		BufferedReader reader = Utils.reader(seedNamesFile);
		seedNames = new HashSet<String>(expectedSeenNamesNum);
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				seedNames.add(line);
			}
		} finally {
			reader.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handle(String line) {
		List<Token> tokens = tokenizer.tokenize(line);
		if (tokens.size() <= 1) {
			return null;
		}
		int patternsDerived = 0;
		for (int i = 0; i < tokens.size(); i++) {
			Token curToken = tokens.get(i);
			if (!tokenizer.isDelimeter(curToken) && seedNames.contains(curToken.getString())) {
				List<Token> pattern = toPattern(tokens, i);
				if (allow(pattern)) {
					patternsDerived++;
					writer.println(toPatternString(pattern));
				} else {
					System.out.println("Filtered pattern: " + toPatternString(pattern));
				}
			}
		}
		if (patternsDerived > 0) {
			return null;
		} else {
			return line;
		}
	}

	private boolean allow(List<Token> pattern) {
		for (Token t : pattern) {
			String tString = t.getString();
			for (int i = 0; i < tString.length(); i++) {
				if (Character.isLetter(tString.charAt(i))) {
					return true;
				}
			}
		}
		return false;
	}

	private List<Token> toPattern(List<Token> tokens, int wildcardIndex) {
		if (wildcardIndex < 0 || wildcardIndex >= tokens.size()) {
			throw new IllegalStateException();
		}
		List<Token> result = Lists.newArrayListWithCapacity(tokens.size());
		for (int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			Token resultToken = new Token(t.getBegin(), t.getEnd(), t.getString());
			if (i == wildcardIndex) {
				resultToken.setString(WILDCARD);
			}
			result.add(resultToken);
		}
		return result;
	}

	private String toPatternString(List<Token> tokens) {
		StringBuilder sb = new StringBuilder();
		for (Token t : tokens) {
			sb.append(t.getString()).append(' ');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		IOUtils.closeQuietly(writer);
	}

}