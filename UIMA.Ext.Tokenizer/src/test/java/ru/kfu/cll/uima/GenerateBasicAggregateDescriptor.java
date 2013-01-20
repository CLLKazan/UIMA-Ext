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

import org.apache.uima.annotator.regex.impl.RegExAnnotator;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.xml.sax.SAXException;

import ru.kfu.cll.uima.segmentation.PunctuationSegmentAnnotator;
import ru.kfu.cll.uima.segmentation.SentenceSplitter;
import ru.kfu.cll.uima.tokenizer.InitialTokenizer;
//import org.apache.uima.annotator.regex.impl.*;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateBasicAggregateDescriptor {

	/**
	 * @param args
	 * @throws ResourceInitializationException
	 */
	public static void main(String[] args) throws UIMAException, IOException, SAXException 
	{
		
		boolean segmentator = false;

		if(segmentator)
		{
			
		TypeSystemDescription tokenizerTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem");
		AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
				InitialTokenizer.class, tokenizerTsDesc);

//		TypeSystemDescription regTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem-complexTypes");
//		AnalysisEngineDescription regDesc = createPrimitiveDescription(
//				org.apache.uima.annotator.regex.impl.RegExAnnotator.class, regTsDesc);

		TypeSystemDescription ssTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
		AnalysisEngineDescription ssDesc = createPrimitiveDescription(SentenceSplitter.class,
				ssTsDesc);
		
		TypeSystemDescription pmTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.segmentation.segmentation-TypeSystem");
		AnalysisEngineDescription pmDesc = createPrimitiveDescription(PunctuationSegmentAnnotator.class,
				pmTsDesc);

		String outputPath = "src/test/resources/token-segment-aggregate.xml";
		AnalysisEngineDescription desc = AnalysisEngineFactory.createAggregateDescription(
				tokenizerDesc, ssDesc, pmDesc);
		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			desc.toXML(out);
		} finally {
			IOUtils.closeQuietly(out);
		}
		
		}
		
		
		else
		{
			//initialtokenizer WS+Regex
			
			TypeSystemDescription tokenizerTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem");
			AnalysisEngineDescription tokenizerDesc = createPrimitiveDescription(
					InitialTokenizer.class, tokenizerTsDesc);
				
			TypeSystemDescription pmTsDesc = createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem");
			AnalysisEngineDescription pmDesc = createPrimitiveDescription(RegExAnnotator.class,
					pmTsDesc);
			
			
			String outputPath = "src/test/resources/token-itok_regex-aggregate.xml";
			AnalysisEngineDescription desc = AnalysisEngineFactory.createAggregateDescription(
					tokenizerDesc, pmDesc);
			FileOutputStream out = new FileOutputStream(outputPath);
			try {
				desc.toXML(out);
			} finally {
				IOUtils.closeQuietly(out);
			}
			
		}
		
	}

}
