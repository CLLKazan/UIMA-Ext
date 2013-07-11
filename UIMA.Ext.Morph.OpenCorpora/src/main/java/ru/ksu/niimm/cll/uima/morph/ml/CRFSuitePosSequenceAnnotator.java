/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.CleartkSequenceAnnotator;
import org.cleartk.classifier.Feature;
import org.cleartk.classifier.Instances;
import org.cleartk.classifier.feature.extractor.CleartkExtractor;
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
import org.uimafit.util.JCasUtil;

import com.google.common.collect.Lists;

import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;

/**
 * @author Rinat Gareev
 * 
 */
public class CRFSuitePosSequenceAnnotator extends CleartkSequenceAnnotator<String> {

	private SimpleFeatureExtractor tokenFeatureExtractor;
	private CleartkExtractor contextFeatureExtractor;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		tokenFeatureExtractor = new FeatureFunctionExtractor(new CoveredTextExtractor(),
				new LowerCaseFeatureFunction(),
				new CapitalTypeFeatureFunction(),
				new NumericTypeFeatureFunction(),
				new CharacterNGramFeatureFunction(Orientation.RIGHT_TO_LEFT, 0, 3));

		contextFeatureExtractor = new CleartkExtractor(Token.class, new SuffixFeatureExtractor(3),
				new CleartkExtractor.Preceding(2), new CleartkExtractor.Following(2));
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		for (Sentence sent : JCasUtil.select(jCas, Sentence.class)) {
			List<List<Feature>> sentSeq = Lists.newArrayList();
			List<String> sentLabels = null;
			if (isTraining()) {
				sentLabels = Lists.newArrayList();
			}

			for (Token tok : JCasUtil.selectCovered(jCas, Token.class, sent)) {
				if (tok instanceof W || tok instanceof NUM) {
					if (isTraining()) {
						List<Word> _tokWords = JCasUtil.selectCovered(Word.class, tok);
						if (_tokWords.isEmpty()) {
							getLogger().warn(String.format("No Words for token %s in %s",
									toPrettyString(tok), getDocumentUri(jCas.getCas())));
							continue;
						}
						if (_tokWords.size() > 1) {
							throw new IllegalStateException(String.format(
									"Illegal list of Words %s covered by token %s in %s",
									_tokWords, toPrettyString(tok), getDocumentUri(jCas.getCas())));
						}
						Word tokWord = _tokWords.get(0);
						FSArray tokWordforms = tokWord.getWordforms();
						if (tokWordforms == null || tokWordforms.size() == 0) {
							throw new IllegalStateException(String.format(
									"No wordforms in Word %s in %s", tokWord,
									getDocumentUri(jCas.getCas())));
						}
						Wordform tokWf = (Wordform) tokWordforms.get(0);
						String outputLabel = extractOutputLabel(tokWf);
						sentLabels.add(outputLabel);
					}
					List<Feature> tokFeatures = Lists.newLinkedList();
					tokFeatures.addAll(tokenFeatureExtractor.extract(jCas, tok));
					tokFeatures.addAll(contextFeatureExtractor.extract(jCas, tok));
					sentSeq.add(tokFeatures);
				}
			}
			if (isTraining()) {
				dataWriter.write(Instances.toInstances(sentLabels, sentSeq));
			} else {
				// TODO
				throw new UnsupportedOperationException();
			}
		}
	}

	private String extractOutputLabel(Wordform wf) {
		// TODO implement this method for other tiers
		return String.valueOf(wf.getPos());
	}
}