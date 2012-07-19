/**
 * 
 */
package ru.kfu.itis.cll.dictionary.utils;

import java.util.List;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface Tokenizer {
	List<Token> tokenize(String str);
}