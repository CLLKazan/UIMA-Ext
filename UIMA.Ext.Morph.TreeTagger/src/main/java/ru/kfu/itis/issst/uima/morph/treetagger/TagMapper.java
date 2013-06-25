/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import org.opencorpora.cas.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface TagMapper {

	void parseTag(String tag, Wordform wf, String token);

}