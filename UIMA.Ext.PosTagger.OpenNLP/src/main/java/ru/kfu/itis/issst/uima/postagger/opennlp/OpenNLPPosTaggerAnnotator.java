/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.RandomAccess;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.CleartkProcessingException;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.Instances;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
import org.cleartk.classifier.feature.extractor.CleartkExtractor.Context;
import org.cleartk.classifier.feature.extractor.CleartkExtractorException;
import org.cleartk.classifier.feature.extractor.simple.CombinedExtractor;
import org.cleartk.classifier.feature.extractor.simple.CoveredTextExtractor;
import org.cleartk.classifier.feature.extractor.simple.SimpleFeatureExtractor;
import org.cleartk.classifier.feature.function.CapitalTypeFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction;
import org.cleartk.classifier.feature.function.CharacterNGramFeatureFunction.Orientation;
import org.cleartk.classifier.feature.function.FeatureFunctionExtractor;
import org.cleartk.classifier.feature.function.LowerCaseFeatureFunction;
import org.cleartk.classifier.feature.function.NumericTypeFeatureFunction;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.cleartk.Disposable;
import ru.ksu.niimm.cll.uima.morph.ml.SuffixFeatureExtractor;
import ru.ksu.niimm.cll.uima.morph.ml.WordAnnotator;
import ru.ksu.niimm.cll.uima.morph.opencorpora.MorphCasUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OpenNLPPosTaggerAnnotator extends CleartkSequenceAnnotator<String> {

	public static final String RESOURCE_KEY_MORPH_DICTIONARY = "MorphDictionary";
	public static final String PARAM_LEFT_CONTEXT_SIZE = "leftContextSize";
	public static final String PARAM_RIGHT_CONTEXT_SIZE = "rightContextSize";
	public static final String PARAM_REUSE_EXISTING_WORD_ANNOTATIONS = "reuseExistingWordAnnotations";
	// config fields
	@ExternalResource(key = RESOURCE_KEY_MORPH_DICTIONARY, mandatory = true)
	private MorphDictionaryHolder morphDictHolder;
	// feature extraction parameters
	@ConfigurationParameter(name = PARAM_LEFT_CONTEXT_SIZE, defaultValue = "2")
	private int leftContextSize = -1;
	@ConfigurationParameter(name = PARAM_RIGHT_CONTEXT_SIZE, defaultValue = "2")
	private int rightContextSize = -1;
	@ConfigurationParameter(name = PARAM_REUSE_EXISTING_WORD_ANNOTATIONS, defaultValue = "false")
	private boolean reuseExistingWordAnnotations;
	// derived
	private MorphDictionary morphDictionary;
	// features
	private SimpleFeatureExtractor tokenFeatureExtractor;
	private CleartkExtractor contextFeatureExtractor;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		morphDictionary = morphDictHolder.getDictionary();
		// check grammems
		checkDictGrammems();

		tokenFeatureExtractor = new FeatureFunctionExtractor(new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new CapitalTypeFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 2),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 1));

		List<SimpleFeatureExtractor> contextFeatureExtractors = Lists.newArrayList();
		contextFeatureExtractors.add(new SuffixFeatureExtractor(3));
		// TODO introduce difference between Null and NotApplicable values

		if (leftContextSize < 0 || rightContextSize < 0) {
			throw new IllegalStateException("context size < 0");
		}
		if (leftContextSize == 0 && rightContextSize == 0) {
			throw new IllegalStateException("left & right context sizes == 0");
		}
		List<Context> contexts = Lists.newArrayList();
		if (leftContextSize > 0) {
			contexts.add(new CleartkExtractor.Preceding(leftContextSize));
		}
		if (rightContextSize > 0) {
			contexts.add(new CleartkExtractor.Following(rightContextSize));
		}
		contextFeatureExtractor = new CleartkExtractor(Word.class,
				new CombinedExtractor(contextFeatureExtractors.toArray(FE_ARRAY)),
				contexts.toArray(new Context[contexts.size()]));
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		if (!isTraining()) {
			if (reuseExistingWordAnnotations) {
				// clean wordforms to avoid erroneous feature extraction or output assignment
				cleanWordforms(jCas);
			} else {
				// ensure that there are no existing annotations
				// // otherwise things may go irregularly
				if (JCasUtil.exists(jCas, Word.class)) {
					throw new IllegalStateException(String.format(
							"CAS '%s' has Word annotations before this annotator",
							getDocumentUri(jCas)));
				}
				// make Word annotations
				WordAnnotator.makeWords(jCas);
			}
		}
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			process(jCas, sent);
		}
	}

	@Override
	public void destroy() {
		if (classifier instanceof Disposable) {
			((Disposable) classifier).dispose();
		}
		super.destroy();
	}

	private void process(JCas jCas, Sentence sent) throws AnalysisEngineProcessException {
		if (isTraining()) {
			trainingProcess(jCas, sent);
		} else {
			taggingProcess(jCas, sent);
		}
	}

	private void trainingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
		List<List<Feature>> sentSeq = Lists.newArrayList();
		List<String> sentLabels = Lists.newArrayList();
		for (Word word : JCasUtil.selectCovered(jCas, Word.class, sent)) {
			// TRAINING
			Wordform tokWf = MorphCasUtils.requireOnlyWordform(word);
			String outputLabel = extractOutputLabel(tokWf);
			sentLabels.add(outputLabel);
			List<Feature> tokFeatures = extractFeatures(jCas, word);
			sentSeq.add(tokFeatures);
		}
		// TRAINING
		dataWriter.write(Instances.toInstances(sentLabels, sentSeq));
	}

	private void taggingProcess(JCas jCas, Sentence sent) throws CleartkProcessingException {
		List<List<Feature>> sentSeq = Lists.newArrayList();
		List<Wordform> wfSeq = Lists.newArrayList();
		for (Word word : JCasUtil.selectCovered(jCas, Word.class, sent)) {
			// TAGGING
			Wordform tokWf = MorphCasUtils.requireOnlyWordform(word);
			wfSeq.add(tokWf);
			List<Feature> tokFeatures = extractFeatures(jCas, word);
			sentSeq.add(tokFeatures);
		}
		List<String> labelSeq = classifier.classify(sentSeq);
		if (labelSeq.size() != wfSeq.size()) {
			throw new IllegalStateException();
		}
		if (!(labelSeq instanceof RandomAccess)) {
			labelSeq = new ArrayList<String>(labelSeq);
		}
		for (int i = 0; i < labelSeq.size(); i++) {
			String label = labelSeq.get(i);
			if (label == null || label.isEmpty() || label.equalsIgnoreCase("null")) {
				// do nothing, it means there is no a new PoS-tag for this wordform
				continue;
			}
			Iterable<String> newGrams = targetGramSplitter.split(label);
			Wordform wf = wfSeq.get(i);
			MorphCasUtils.addGrammemes(jCas, wf, newGrams);
		}
	}

	private List<Feature> extractFeatures(JCas jCas, Word word) throws CleartkExtractorException {
		List<Feature> tokFeatures = Lists.newLinkedList();
		tokFeatures.addAll(tokenFeatureExtractor.extract(jCas, word));
		tokFeatures.addAll(contextFeatureExtractor.extract(jCas, word));
		return tokFeatures;
	}

	private String extractOutputLabel(Wordform wf) {
		BitSet wfBits = toGramBits(morphDictionary, FSUtils.toList(wf.getGrammems()));
		if (wfBits.isEmpty()) {
			return null;
		}
		return targetGramJoiner.join(morphDictionary.toGramSet(wfBits));
	}

	private static final String targetGramDelim = "&";
	private static final Joiner targetGramJoiner = Joiner.on(targetGramDelim);
	private static final Splitter targetGramSplitter = Splitter.on(targetGramDelim);

	private void checkDictGrammems() {
		for (int grId = 0; grId < morphDictionary.getGrammemMaxNumId(); grId++) {
			Grammeme gr = morphDictionary.getGrammem(grId);
			if (gr != null && gr.getId().contains(targetGramDelim)) {
				throw new IllegalStateException(String.format(
						"Grammeme %s contains character that is used as delimiter in this class",
						gr.getId()));
			}
		}
	}

	static final Splitter posCatSplitter = Splitter.on('&').trimResults();
	private static final SimpleFeatureExtractor[] FE_ARRAY = new SimpleFeatureExtractor[0];

	private void cleanWordforms(JCas jCas) {
		for (Word w : JCasUtil.select(jCas, Word.class)) {
			Wordform wf = new Wordform(jCas);
			wf.setWord(w);
			w.setWordforms(FSUtils.toFSArray(jCas, wf));
		}
	}
}