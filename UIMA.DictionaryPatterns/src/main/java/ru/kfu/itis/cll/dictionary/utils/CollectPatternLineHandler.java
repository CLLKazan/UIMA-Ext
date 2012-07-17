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

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CollectPatternLineHandler implements LineHandler {

	public static final String WILDCARD = "%%%";

	private File seedNamesFile;
	private int expectedSeenNamesNum;
	private Tokenizer tokenizer;
	private PrintWriter writer;
	// derived
	private Set<String> seedNames;

	public CollectPatternLineHandler(File seedNamesFile, int expectedSeenNamesNum,
			File outputFile)
			throws IOException {
		this.seedNamesFile = seedNamesFile;
		this.expectedSeenNamesNum = expectedSeenNamesNum;
		tokenizer = new Tokenizer(true);
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
			return line;
		}
		for (int i = 0; i < tokens.size(); i++) {
			Token curToken = tokens.get(i);
			if (!tokenizer.isDelimeter(curToken) && seedNames.contains(curToken)) {
				String pattern = toPattern(tokens, i);
				writer.println(pattern);
			}
		}
		return line;
	}

	private String toPattern(List<Token> tokens, int wildcardIndex) {
		if (wildcardIndex < 0 || wildcardIndex >= tokens.size()) {
			throw new IllegalStateException();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.size(); i++) {
			String curToken;
			if (i == wildcardIndex) {
				curToken = WILDCARD;
			} else {
				curToken = tokens.get(i).getString();
			}
			sb.append(curToken);
		}
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