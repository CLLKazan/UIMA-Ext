/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Map;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class DefaultWordformStore implements WordformStore, Serializable {

	private static final long serialVersionUID = 1295117920556106439L;

	protected Map<String, BitSet> strKeyMap;

	@Override
	public BitSet getPosBits(String wf) {
		return strKeyMap.get(wf);
	}
}