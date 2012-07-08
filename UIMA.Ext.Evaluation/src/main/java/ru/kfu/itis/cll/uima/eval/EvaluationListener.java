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

	public void onMissing(Type type, Annotation goldAnno);

	public void onMatching(Type type, SortedSet<Annotation> goldAnnos,
			SortedSet<Annotation> sysAnnos);

	public void onSpurious(Type type, Annotation sysAnno);
}