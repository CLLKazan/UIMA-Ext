/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.initialize.ConfigurationParameterInitializer;
import org.uimafit.descriptor.ExternalResource;

import com.google.common.collect.ImmutableSet;

import opennlp.tools.postag.TagDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphDictionaryAdapter implements TagDictionary {

	public static final String RESOURCE_MORPH_DICTIONARY = "morphDict";
	// XXX
	static final String PARAM_GRAM_CATEGORIES = "gram.categories";

	@ExternalResource()
	private Set<String> gramCategories;

	public MorphDictionaryAdapter(UimaContext ctx) throws ResourceInitializationException {
		ConfigurationParameterInitializer.initialize(ctx, ctx);
	}

	@Override
	public String[] getTags(String word) {
		word = WordUtils.normalizeToDictionaryForm(word);
		// XXX
		throw new UnsupportedOperationException();
	}

	public Set<String> getGramCategories() {
		return ImmutableSet.copyOf(gramCategories);
	}
}
