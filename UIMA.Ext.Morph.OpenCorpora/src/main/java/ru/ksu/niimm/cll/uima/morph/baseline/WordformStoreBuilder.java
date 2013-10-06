/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.io.File;
import java.util.BitSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
interface WordformStoreBuilder {
	void increment(String wordString, BitSet posBits);

	void persist(File outFile) throws Exception;
}