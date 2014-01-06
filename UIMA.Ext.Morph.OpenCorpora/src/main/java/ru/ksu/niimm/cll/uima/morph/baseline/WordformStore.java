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
interface WordformStore {

	BitSet getPosBits(String wf);

	<T> T getProperty(String key, Class<T> valueClass);

	void setProperty(String key, Object value);

	void persist(File outFile) throws Exception;
}