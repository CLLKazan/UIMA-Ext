package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.uimafit.factory.AnalysisEngineFactory;

import ru.kfu.itis.issst.uima.segmentation.SentenceSplitterAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

public class Unitizer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static AnalysisEngineDescription createTokenizerSentenceSplitterAED()
			throws UIMAException, IOException {

		AnalysisEngineDescription tokenizerDesc = TokenizerAPI.getAEDescription();

		AnalysisEngineDescription ssDesc = SentenceSplitterAPI.getAEDescription();

		return AnalysisEngineFactory.createAggregateDescription(tokenizerDesc, ssDesc);
	}
}
