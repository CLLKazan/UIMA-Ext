/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static ru.kfu.itis.issst.uima.morph.commons.TestUtils.getSerializedDictionaryFile;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModelDeserializer;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GramModelLoader4Tests {

	public static final GramModel gm;

	static {
		try {
			gm = GramModelDeserializer.from(getSerializedDictionaryFile());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static void init() {
		// this is just 'sugar' method
		// initialization will be done when this class is accessed the first time
	}

	private GramModelLoader4Tests() {
	}
}
