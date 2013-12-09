/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.util;

import java.util.BitSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
public class BitUtils {
	
	/**
	 * @param arg
	 * @param filter
	 * @return true only if arg contains all bits from filter
	 */
	public static boolean contains(BitSet arg, BitSet filter) {
		for (int i = filter.nextSetBit(0); i >= 0; i = filter.nextSetBit(i + 1)) {
			if (!arg.get(i)) {
				return false;
			}
		}
		return true;
	}
	
	private BitUtils(){
	}

}
