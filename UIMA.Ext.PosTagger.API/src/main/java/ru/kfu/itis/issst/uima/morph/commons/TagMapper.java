/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import java.util.Set;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface TagMapper {

	Set<String> parseTag(String tag, String token);

	String toTag(Set<String> grammems);
}