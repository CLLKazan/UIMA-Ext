/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.BitSet;
import java.util.Set;

import org.apache.uima.cas.CASException;
import org.opencorpora.cas.Wordform;

import ru.kfu.itis.cll.uima.cas.FSUtils;

/**
 * Operations extending MorphDictionary interface
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphDictionaryUtils {

	public static BitSet toGramBits(GramModel gm, Iterable<String> grams) {
		BitSet result = new BitSet(gm.getGrammemMaxNumId());
		for (String gr : grams) {
			result.set(gm.getGrammemNumId(gr));
		}
		return result;
	}

	public static void applyGrammems(Set<String> grams, Wordform wf) {
		if (grams == null || grams.isEmpty()) {
			return;
		}
		try {
			wf.setGrammems(FSUtils.toStringArray(wf.getCAS().getJCas(), grams));
		} catch (CASException e) {
			throw new RuntimeException(e);
		}
	}

	private MorphDictionaryUtils() {
	}

}