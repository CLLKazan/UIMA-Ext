package ru.kfu.itis.issst.corpus.statistics.cpe;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import ru.kfu.cll.uima.segmentation.SentenceSplitter;
import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
import ru.kfu.cll.uima.tokenizer.PostTokenizer;

public class Unitizer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static AnalysisEngineDescription createTokenizerSentenceSplitterAED()
			throws ResourceInitializationException {
		TypeSystemDescription tokenizerTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem");
		AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
				InitialTokenizer.class, tokenizerTsDesc);
		AnalysisEngineDescription postTokenizerDesc = createPrimitiveDescription(
				PostTokenizer.class, tokenizerTsDesc);

		TypeSystemDescription ssTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
		AnalysisEngineDescription ssDesc = createPrimitiveDescription(
				SentenceSplitter.class, ssTsDesc);

		return AnalysisEngineFactory.createAggregateDescription(tokenizerDesc,
				postTokenizerDesc, ssDesc);
	}
}
