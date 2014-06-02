/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.model.ArtifactSerializer;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class POSTaggerFactory extends BaseToolFactory {

	private static final String TAG_DICTIONARY_ENTRY_NAME = "tags.tagdict";
	private static final String FEATURE_EXTRACTORS_ENTRY_NAME = "feature.extractors";

	private TagDictionary tagDictionary;
	private BeamSearchContextGenerator<Token> contextGenerator;

	public BeamSearchContextGenerator<Token> getContextGenerator() {
		if (contextGenerator == null && artifactProvider != null) {
			contextGenerator = artifactProvider.getArtifact(FEATURE_EXTRACTORS_ENTRY_NAME);
		}
		return contextGenerator;
	}

	public SequenceValidator<Token> getSequenceValidator() {
		return new DictionaryBasedTokenSequenceValidator(getTagDictionary());
	}

	public TagDictionary getTagDictionary() {
		if (tagDictionary == null && artifactProvider != null) {
			tagDictionary = artifactProvider.getArtifact(TAG_DICTIONARY_ENTRY_NAME);
		}
		return tagDictionary;
	}

	@Override
	public Map<String, Object> createArtifactMap() {
		Map<String, Object> artMap = super.createArtifactMap();
		if (tagDictionary != null) {
			artMap.put(TAG_DICTIONARY_ENTRY_NAME, tagDictionary);
		}
		artMap.put(FEATURE_EXTRACTORS_ENTRY_NAME, contextGenerator);
		return artMap;
	}

	@Override
	public void validateArtifactMap() throws InvalidFormatException {
		Object tagDictEntry = artifactProvider.getArtifact(TAG_DICTIONARY_ENTRY_NAME);
		if (tagDictEntry != null) {
			if (!(tagDictEntry instanceof MorphDictionaryAdapter)) {
				throw new InvalidFormatException(String.format(
						"Unknown type of tag dictionary: %s", tagDictEntry.getClass()));
			}
			// TODO check dict compliance
		}
		Object featExtractorsEntry = artifactProvider.getArtifact(FEATURE_EXTRACTORS_ENTRY_NAME);
		if (featExtractorsEntry == null) {
			throw new InvalidFormatException("No featureExtractors in artifacts map");
		}
		if (!(featExtractorsEntry instanceof FeatureExtractorsBasedContextGenerator)) {
			throw new InvalidFormatException(String.format(
					"Unknown type of feature extractors aggregate: %s",
					featExtractorsEntry.getClass()));
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, ArtifactSerializer> createArtifactSerializersMap() {
		Map<String, ArtifactSerializer> artSerMap = super.createArtifactSerializersMap();
		artSerMap.put("tagdict", new MorphDictionarySerializer());
		artSerMap.put("extractors", new FeatureExtractorsSerializer());
		return artSerMap;
	}

	static class FeatureExtractorsSerializer implements
			ArtifactSerializer<FeatureExtractorsBasedContextGenerator> {

		@Override
		public FeatureExtractorsBasedContextGenerator create(InputStream in) throws IOException,
				InvalidFormatException {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

		@Override
		public void serialize(FeatureExtractorsBasedContextGenerator artifact, OutputStream out)
				throws IOException {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException();
		}

	}

	static class MorphDictionarySerializer implements ArtifactSerializer<MorphDictionaryAdapter> {

		private static final Joiner gramCatJoiner = Joiner.on(',');
		private static final Splitter gramCatSplitter = Splitter.on(',');

		@Override
		public MorphDictionaryAdapter create(InputStream in) throws IOException,
				InvalidFormatException {
			Properties props = new Properties();
			props.load(in);
			Set<String> gramCats = ImmutableSet.copyOf(gramCatSplitter.split(
					props.getProperty(MorphDictionaryAdapter.PARAM_GRAM_CATEGORIES)));
			return new MorphDictionaryAdapter(gramCats);
		}

		@Override
		public void serialize(MorphDictionaryAdapter mda, OutputStream out) throws IOException {
			Properties props = new Properties();
			props.setProperty(MorphDictionaryAdapter.PARAM_GRAM_CATEGORIES,
					gramCatJoiner.join(mda.getGramCategories()));
			props.store(out, null);
		}
	}
}
