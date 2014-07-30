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
public class GetSingleWordNames extends HandlersAggregate {

	private File outputFile;

	/**
	 * @param dictFile
	 * @param outputFile
	 */
	public GetSingleWordNames(File dictFile, File outputFile) {
		super(dictFile);
		this.outputFile = outputFile;
	}

	@Override
	protected List<LineHandler> getHandlers() {
		try {
			return Arrays.asList(new FilterQuotedNames(), new CollectSingleWordNames(outputFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		new GetSingleWordNames(new File(args[0]), new File(args[1])).run();
	}

}