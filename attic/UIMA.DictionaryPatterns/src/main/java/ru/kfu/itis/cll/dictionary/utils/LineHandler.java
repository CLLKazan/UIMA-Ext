/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
public interface LineHandler {
	
	String handle(String line);

	void close();
}