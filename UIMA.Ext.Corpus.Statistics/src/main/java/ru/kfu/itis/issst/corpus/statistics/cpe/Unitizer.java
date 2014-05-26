package ru.kfu.itis.issst.corpus.statistics.cpe;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import ru.kfu.itis.issst.uima.segmentation.SentenceSplitter;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

public class Unitizer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static AnalysisEngineDescription createTokenizerSentenceSplitterAED()
			throws UIMAException, IOException {

		AnalysisEngineDescription tokenizerDesc = TokenizerAPI.getAEDescription();

		TypeSystemDescription ssTsDesc = createTypeSystemDescription("ru.kfu.itis.issst.uima.segmentation.segmentation-TypeSystem");
		AnalysisEngineDescription ssDesc = createPrimitiveDescription(
				SentenceSplitter.class, ssTsDesc);

		return AnalysisEngineFactory.createAggregateDescription(tokenizerDesc, ssDesc);
	}
}
