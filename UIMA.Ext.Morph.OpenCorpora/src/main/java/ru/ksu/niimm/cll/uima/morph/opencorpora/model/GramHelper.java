/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

import java.util.BitSet;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GramHelper {

	private MorphDictionary dict;
	// derived
	private BitSet posBits;

	public GramHelper(MorphDictionary dict) {
		this.dict = dict;
		//
		posBits = dict.getGrammemWithChildrenBits(POST, false);
	}

	public String toPosString(BitSet gramBS) {
		int topCatId = getSharedBit(gramBS, posBits);
		String topCat = null;
		if (topCatId >= 0) {
			Grammeme topCatGr = dict.getGrammem(topCatId);
			if (topCatGr == null) {
				throw new IllegalStateException(String.format(
						"Unknown pos category with id %s", topCatId));
			}
			topCat = topCatGr.getId();
		}
		// XXX
		// TODO
		throw new UnsupportedOperationException();
	}

	private static int getSharedBit(BitSet target, BitSet categoryBits) {
		for (int i = categoryBits.nextSetBit(0); i >= 0; i = categoryBits.nextSetBit(i + 1)) {
			if (target.get(i)) {
				return i;
			}
		}
		return -1;
	}
}