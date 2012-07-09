/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.util.EventListener;
import java.util.SortedSet;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface EvaluationListener extends EventListener {

	public void onMissing(String docUri, Type type, Annotation goldAnno);

	public void onMatching(String docUri, Type type, SortedSet<Annotation> goldAnnos,
			SortedSet<Annotation> sysAnnos);

	public void onSpurious(String docUri, Type type, Annotation sysAnno);
}