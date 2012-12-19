/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.tools;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.exit;
import static java.lang.System.gc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.apache.commons.io.IOUtils;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SearchDictionary {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		if (args.length != 2) {
			err.println("Usage: <dict-file> <search-string>");
			exit(1);
		}
		File dictFile = new File(args[0]);
		String searchStr = args[1];
		if (!dictFile.isFile()) {
			err.println(format("%s is not a file", dictFile));
			exit(1);
		}
		new SearchDictionary(dictFile).search(searchStr);
	}

	private MorphDictionary dict;

	private SearchDictionary(File dictFile) throws IOException, ClassNotFoundException {
		InputStream is = new BufferedInputStream(
				new FileInputStream(dictFile), 32768);
		ObjectInputStream ois = new ObjectInputStream(is);
		try {
			dict = (MorphDictionary) ois.readObject();
		} finally {
			IOUtils.closeQuietly(ois);
		}
		log("Heap usage: %s", Runtime.getRuntime().totalMemory());
		gc();
		log("Heap usage: %s", Runtime.getRuntime().totalMemory());
	}

	private void search(String searchStr) {
		// TODO
	}

	private void log(String msg, Object... args) {
		String printMsg;
		if (args.length == 0) {
			printMsg = msg;
		} else {
			printMsg = format(msg, args);
		}
		System.out.println(printMsg);
	}
}