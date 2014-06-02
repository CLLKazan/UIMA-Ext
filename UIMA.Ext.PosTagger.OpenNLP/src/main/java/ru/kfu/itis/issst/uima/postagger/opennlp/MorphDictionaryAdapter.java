/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import opennlp.tools.postag.TagDictionary;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphDictionaryAdapter implements TagDictionary {

	static final String PARAM_GRAM_CATEGORIES = "gram.categories";

	private Set<String> gramCategories;

	public MorphDictionaryAdapter(Set<String> gramCategories) {
		this.gramCategories = ImmutableSet.copyOf(gramCategories);
	}

	@Override
	public String[] getTags(String word) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<String> getGramCategories() {
		return ImmutableSet.copyOf(gramCategories);
	}
}
