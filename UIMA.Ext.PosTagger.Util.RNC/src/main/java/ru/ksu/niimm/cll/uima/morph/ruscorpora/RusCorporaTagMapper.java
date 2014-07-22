/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import org.opencorpora.cas.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface RusCorporaTagMapper {

	void mapFromRusCorpora(RusCorporaWordform srcWf, Wordform targetWf);

}