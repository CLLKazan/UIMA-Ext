/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph;

import ru.ksu.niimm.cll.uima.morph.opencorpora.AnnotationAdapter;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class AnnotationAdapterBase implements AnnotationAdapter {

	protected MorphDictionary dict;

	@Override
	public void init(MorphDictionary dict) {
		this.dict = dict;
	}

}