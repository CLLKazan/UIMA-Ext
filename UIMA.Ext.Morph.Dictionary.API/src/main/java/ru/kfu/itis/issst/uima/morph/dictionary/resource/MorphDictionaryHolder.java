/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.dictionary.resource;

import org.apache.uima.resource.SharedResourceObject;

/**
 * @author Rinat Gareev
 * 
 */
public interface MorphDictionaryHolder extends SharedResourceObject {

	MorphDictionary getDictionary();

}