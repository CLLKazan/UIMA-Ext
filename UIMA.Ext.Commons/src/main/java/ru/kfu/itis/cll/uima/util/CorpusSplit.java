/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.util.Set;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface CorpusSplit {

	/**
	 * @return set of training resource paths relative to a corpus root
	 */
	Set<String> getTrainingSetPaths();

	/**
	 * @return set of testing resource paths relative to a corpus root
	 */
	Set<String> getTestingSetPaths();

}