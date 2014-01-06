/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.util.BitSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
interface WordformStoreBuilder {
	void increment(String wordString, BitSet posBits);

	WordformStore build();
}