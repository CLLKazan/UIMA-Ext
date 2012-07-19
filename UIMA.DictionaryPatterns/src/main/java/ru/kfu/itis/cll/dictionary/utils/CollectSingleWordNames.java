/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CollectSingleWordNames implements LineHandler {

	private PrintWriter writer;
	private TokenizerImpl tokenizer;

	public CollectSingleWordNames(File outputFile) throws IOException {
		writer = Utils.writer(outputFile);
		tokenizer = new TokenizerImpl();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handle(String line) {
		List<Token> tokens = tokenizer.tokenize(line);
		if (tokens.size() == 1) {
			writer.println(tokens.get(0).getString());
		}
		return line;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		IOUtils.closeQuietly(writer);
	}

}