/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GetSeedPatterns extends HandlersAggregate {

	private File outputFile;
	private File singleWordNamesFile;

	/**
	 * @param dictFile
	 * @param outputFile
	 */
	public GetSeedPatterns(File dictFile, File singleWordNamesFile, File outputFile) {
		super(dictFile);
		this.singleWordNamesFile = singleWordNamesFile;
		this.outputFile = outputFile;
	}

	@Override
	protected List<LineHandler> getHandlers() {
		try {
			return Arrays.asList(new FilterQuotedNames(),
					new CollectPatternLineHandler(
							singleWordNamesFile,
							500000, outputFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		new GetSeedPatterns(new File(args[0]), new File(args[1]), new File(args[2])).run();
	}

}