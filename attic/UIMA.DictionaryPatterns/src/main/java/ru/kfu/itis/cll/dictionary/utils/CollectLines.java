/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.IOUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class CollectLines implements LineHandler {

	private PrintWriter writer;

	public CollectLines(File outputFile) throws IOException {
		writer = Utils.writer(outputFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String handle(String line) {
		writer.println(line);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		IOUtils.closeQuietly(writer);
	}

}