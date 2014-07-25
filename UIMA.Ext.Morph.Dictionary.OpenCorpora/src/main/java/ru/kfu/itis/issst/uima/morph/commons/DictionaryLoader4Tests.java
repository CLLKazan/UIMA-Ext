/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static ru.kfu.itis.issst.uima.morph.commons.TestUtils.getSerializedDictionaryFile;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DictionaryDeserializer;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryLoader4Tests {

	public static final MorphDictionary dict;
	public static final GramModel gm;

	static {
		try {
			dict = DictionaryDeserializer.from(getSerializedDictionaryFile());
			gm = dict.getGramModel();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void init() {
		// this is just 'sugar' method
		// initialization will be done when this class is accessed the first time
	}

	private DictionaryLoader4Tests() {
	}
}
