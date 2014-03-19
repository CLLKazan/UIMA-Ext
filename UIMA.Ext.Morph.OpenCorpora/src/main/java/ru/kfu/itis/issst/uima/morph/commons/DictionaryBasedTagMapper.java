/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.initialize.ExternalResourceInitializer;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.initializable.Initializable;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryBasedTagMapper implements TagMapper, Initializable {

	public static final String CLASS_NAME = DictionaryBasedTagMapper.class.getName();
	public static final String RESOURCE_KEY_MORPH_DICTIONARY = "MorphDictionary";
	// config fields
	@ExternalResource(key = RESOURCE_KEY_MORPH_DICTIONARY, mandatory = true)
	private MorphDictionaryHolder dictHolder;
	// derived
	private MorphDictionary dict;

	// for UIMA
	public DictionaryBasedTagMapper() {
	}

	// for stand-alone usage
	public DictionaryBasedTagMapper(MorphDictionary dict) {
		this.dict = dict;
	}

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		ExternalResourceInitializer.initialize(ctx, this);
		dict = dictHolder.getDictionary();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> parseTag(String tag, String token) {
		return parseTag(tag);
	}

	public static Set<String> parseTag(String tag) {
		if (tag == null || tag.equalsIgnoreCase("null")) {
			return Sets.newLinkedHashSet();
		}
		return Sets.newLinkedHashSet(targetGramSplitter.split(tag));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toTag(Set<String> grams) {
		BitSet wfBits = toGramBits(dict, grams);
		return toTag(wfBits);
	}

	public String toTag(BitSet wfBits) {
		if (wfBits.isEmpty()) {
			return null;
		}
		return targetGramJoiner.join(dict.toGramSet(wfBits));
	}

	private static final String targetGramDelim = "&";
	private static final Joiner targetGramJoiner = Joiner.on(targetGramDelim);
	private static final Splitter targetGramSplitter = Splitter.on(targetGramDelim);
}
