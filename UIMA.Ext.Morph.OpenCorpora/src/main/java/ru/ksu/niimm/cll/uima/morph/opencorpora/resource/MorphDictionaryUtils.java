/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.BitSet;

/**
 * Operations extending MorphDictionary interface
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphDictionaryUtils {

	public static BitSet toGramBits(MorphDictionary dict, Iterable<String> grams) {
		BitSet result = new BitSet(dict.getGrammemMaxNumId());
		for (String gr : grams) {
			result.set(dict.getGrammemNumId(gr));
		}
		return result;
	}

	private MorphDictionaryUtils() {
	}

}