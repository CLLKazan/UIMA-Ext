/**
 * 
 */
package ru.kfu.cll.uima;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.xml.sax.SAXException;

import ru.kfu.cll.uima.segmentation.SentenceSplitter;
import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
import ru.kfu.cll.uima.tokenizer.PostTokenizer;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateBasicAggregateDescriptor {

	/**
	 * @param args
	 * @throws ResourceInitializationException
	 */
	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		TypeSystemDescription tokenizerTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem");
		AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
				InitialTokenizer.class, tokenizerTsDesc);

		AnalysisEngineDescription postTokenizerDesc = createPrimitiveDescription(
				PostTokenizer.class, tokenizerTsDesc);

		TypeSystemDescription ssTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
		AnalysisEngineDescription ssDesc = createPrimitiveDescription(SentenceSplitter.class,
				ssTsDesc);

		String outputPath = "src/test/resources/basic-aggregate.xml";
		AnalysisEngineDescription desc = AnalysisEngineFactory.createAggregateDescription(
				tokenizerDesc, postTokenizerDesc, ssDesc);
		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

}
