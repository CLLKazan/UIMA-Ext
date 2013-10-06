/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.util.BitSet;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
interface WordformStore {

	BitSet getPosBits(String wf);

}