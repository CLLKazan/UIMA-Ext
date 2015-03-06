/**
 * 
 */
package ru.kfu.itis.cll.uima.annotator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.component.CasAnnotator_ImplBase;

/**
 * An annotator that checks whether a document text contains '%ERROR%' substring
 * and if it does then throws {@link AnalysisEngineProcessException}.
 * <p>
 * To be used in tests.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ErrorProducer extends CasAnnotator_ImplBase {

	public static final String ERROR_SUBSTRING = "%ERROR%";

	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		if (cas.getDocumentText().contains(ERROR_SUBSTRING)) {
			throw new AnalysisEngineProcessException(
					new Exception("ErrorProducer is on duty!"));
		}
	}
}
