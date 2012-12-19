/**
 * 
 */
package ru.kfu.itis.cll.uima.annotator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.util.ExtendedLogger;

/**
 * This annotator will print to UIMA logger CAS text and each annotations.
 * Mainly it is useful for tests development.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AnnotationLogger extends CasAnnotator_ImplBase {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		ExtendedLogger log = getLogger();
		log.info("CAS text:");
		log.info(cas.getDocumentText());
		for (AnnotationFS anno : cas.getAnnotationIndex()) {
			log.info(anno);
		}
		log.info("Logging for particular CAS is finished");
	}

}