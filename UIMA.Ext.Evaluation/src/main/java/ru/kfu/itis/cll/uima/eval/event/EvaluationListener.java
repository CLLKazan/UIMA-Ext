/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import java.util.EventListener;

import org.apache.uima.cas.text.AnnotationFS;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface EvaluationListener extends EventListener {

	public void onMissing(String docUri, AnnotationFS goldAnno);

	public void onExactMatch(String docUri, AnnotationFS goldAnno, AnnotationFS sysAnno);

	public void onPartialMatch(String docUri, AnnotationFS goldAnno, AnnotationFS sysAnno);

	public void onSpurious(String docUri, AnnotationFS sysAnno);

	public void onEvaluationComplete();
}