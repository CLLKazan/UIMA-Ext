/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import org.apache.uima.resource.SharedResourceObject;

/**
 * @author Rinat Gareev
 * 
 */
public interface MorphDictionaryHolder extends SharedResourceObject {

	MorphDictionary getDictionary();

}