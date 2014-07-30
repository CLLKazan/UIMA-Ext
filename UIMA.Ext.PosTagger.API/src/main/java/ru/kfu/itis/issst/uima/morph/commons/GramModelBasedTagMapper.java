/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.component.initialize.ExternalResourceInitializer;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.factory.initializable.Initializable;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GramModelBasedTagMapper implements TagMapper, Initializable {

	/**
	 * Add dependency on an external resource with {@link MorphDictionaryHolder}
	 * API
	 * 
	 * @param clientResourceDesc
	 *            description of a resource (e.g., annotator) that initialize
	 *            this tag mapper.
	 * @throws ResourceInitializationException
	 */
	public static void declareResourceDependencies(ResourceSpecifier clientResourceDesc)
			throws ResourceInitializationException {
		try {
			ExternalResourceFactory.createDependency(clientResourceDesc,
					GramModelBasedTagMapper.RESOURCE_GRAM_MODEL,
					GramModelHolder.class);
		} catch (InvalidXMLException e) {
			throw new ResourceInitializationException(e);
		}
	}

	/*
	ExternalResourceFactory.bindResource(clientResourceDesc,
			GramModelBasedTagMapper.RESOURCE_GRAM_MODEL, gramModelDesc);
			*/

	public static final String CLASS_NAME = GramModelBasedTagMapper.class.getName();
	public static final String RESOURCE_GRAM_MODEL = "gramModel";
	// config fields
	@ExternalResource(key = RESOURCE_GRAM_MODEL, mandatory = true)
	private GramModelHolder gramModelHolder;
	// derived
	private GramModel gramModel;

	// for UIMA
	public GramModelBasedTagMapper() {
	}

	// for stand-alone usage
	public GramModelBasedTagMapper(GramModel gramModel) {
		this.gramModel = gramModel;
	}

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		ExternalResourceInitializer.initialize(ctx, this);
		gramModel = gramModelHolder.getGramModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> parseTag(String tag, String token) {
		return parseTag(tag);
	}

	public static Set<String> parseTag(String tag) {
		if (StringUtils.isEmpty(tag) || tag.equalsIgnoreCase("null")) {
			return Sets.newLinkedHashSet();
		}
		return Sets.newLinkedHashSet(targetGramSplitter.split(tag));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toTag(Set<String> grams) {
		BitSet wfBits = toGramBits(gramModel, grams);
		return toTag(wfBits);
	}

	public String toTag(BitSet wfBits) {
		if (wfBits.isEmpty()) {
			return null;
		}
		return targetGramJoiner.join(gramModel.toGramSet(wfBits));
	}

	public static final String targetGramDelim = "&";
	public static final Joiner targetGramJoiner = Joiner.on(targetGramDelim);
	public static final Splitter targetGramSplitter = Splitter.on(targetGramDelim);
}
