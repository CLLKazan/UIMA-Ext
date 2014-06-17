/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import java.io.File;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TestUtils {

	public static final String SYSPROP_DICTIONARY_HOME = "opencorpora.home";
	public static final String FILENAME_SERIALIZED_DICTIONARY = "dict.opcorpora.ser";

	static File getSerializedDictionaryFile() {
		String dictHomePath = System.getProperty(SYSPROP_DICTIONARY_HOME);
		if (dictHomePath == null) {
			String errMsg = String.format("Setup '%s' system property", SYSPROP_DICTIONARY_HOME);
			System.err.println(errMsg);
			throw new IllegalStateException(errMsg);
		}
		File dictHomeDir = new File(dictHomePath);
		if (!dictHomeDir.isDirectory()) {
			String errMsg = String.format("%s is not an existing directory", dictHomeDir);
			System.err.println(errMsg);
			throw new IllegalStateException(errMsg);
		}
		return new File(dictHomeDir, FILENAME_SERIALIZED_DICTIONARY);
	}

	private TestUtils() {
	}
}
