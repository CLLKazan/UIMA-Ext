/**
 * 
 */
package ru.kfu.itis.issst.uima.segmentation;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SentenceSplitterAPI {

	public static final String AE_SENTENCE_SPLITTER = "ru.kfu.itis.issst.uima.segmentation.sentence-splitter-ae";

	public static final String TYPESYSTEM_SENTENCES = "ru.kfu.itis.issst.uima.segmentation.segmentation-TypeSystem";

	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_SENTENCES);
	}

	public static AnalysisEngineDescription getAEDescription() throws UIMAException, IOException {
		return AnalysisEngineFactory.createAnalysisEngineDescription(AE_SENTENCE_SPLITTER);
	}

	public static Import getAEImport() {
		Import result = new Import_impl();
		result.setName(AE_SENTENCE_SPLITTER);
		return result;
	}

	private SentenceSplitterAPI() {
	}

}
