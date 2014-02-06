/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ExternalResource;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
abstract class BaselineAnnotator extends JCasAnnotator_ImplBase {

	public static final String RESOURCE_MORPH_DICTIONARY = "MorphDictionary";

	// config fields
	@ExternalResource(key = RESOURCE_MORPH_DICTIONARY)
	private MorphDictionaryHolder dictHolder;
	// derived
	protected MorphDictionary dict;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		dict = dictHolder.getDictionary();
	}
}
