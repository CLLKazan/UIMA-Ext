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

import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SentenceSplitterAPI {

	/**
	 * A name of analysis engine description that can be imported. An
	 * implementation of sentence-splitter should provide its description at
	 * this location either in classpath or UIMA datapath.
	 */
	public static final String AE_SENTENCE_SPLITTER = "ru.kfu.itis.issst.uima.segmentation.sentence-splitter-ae";

	/**
	 * A name of type-system description that define types produced by
	 * implementations of sentence splitter.
	 */
	public static final String TYPESYSTEM_SENTENCES = "ru.kfu.itis.issst.uima.segmentation.segmentation-TypeSystem";

	/**
	 * @return type-system description instance
	 */
	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_SENTENCES);
	}

	/**
	 * @return AE description instance
	 * @throws UIMAException
	 * @throws IOException
	 */
	public static AnalysisEngineDescription getAEDescription() throws UIMAException, IOException {
		return AnalysisEngineFactory.createAnalysisEngineDescription(AE_SENTENCE_SPLITTER);
	}

	/**
	 * @return import instance. This is preferred way to include the AE into
	 *         pipeline, especially when a pipeline descriptor is expected to be
	 *         serialized into XML.
	 * @see PipelineDescriptorUtils#createAggregateDescription(java.util.List,
	 *      java.util.List)
	 */
	public static Import getAEImport() {
		Import result = new Import_impl();
		result.setName(AE_SENTENCE_SPLITTER);
		return result;
	}

	private SentenceSplitterAPI() {
	}

}
