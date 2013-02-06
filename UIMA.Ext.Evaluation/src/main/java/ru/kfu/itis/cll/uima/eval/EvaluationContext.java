/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.apache.uima.cas.text.AnnotationFS;

import ru.kfu.itis.cll.uima.eval.event.EvaluationListener;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationContext {

	private EventListenerSupport<EvaluationListener> listenerSupport =
			new EventListenerSupport<EvaluationListener>(EvaluationListener.class);

	// state 
	private String currentDocUri;

	public void addListener(EvaluationListener newListener) {
		listenerSupport.addListener(newListener);
	}

	public void reportMissing(AnnotationFS goldAnno) {
		listenerSupport.fire().onMissing(currentDocUri, goldAnno);
	}

	public void reportExactMatch(AnnotationFS goldAnno,
			AnnotationFS sysAnno) {
		listenerSupport.fire().onExactMatch(currentDocUri, goldAnno, sysAnno);
	}

	public void reportPartialMatch(AnnotationFS goldAnno,
			AnnotationFS sysAnno) {
		listenerSupport.fire().onPartialMatch(currentDocUri, goldAnno, sysAnno);
	}

	public void reportSpurious(AnnotationFS sysAnno) {
		listenerSupport.fire().onSpurious(currentDocUri, sysAnno);
	}

	public void reportEvaluationComplete() {
		listenerSupport.fire().onEvaluationComplete();
	}

	public void setCurrentDocUri(String docUri) {
		currentDocUri = docUri;
	}

	public String getCurrentDocUri() {
		return currentDocUri;
	}
}