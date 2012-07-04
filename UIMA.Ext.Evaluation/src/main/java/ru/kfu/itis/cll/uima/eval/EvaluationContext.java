/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.util.Set;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationContext {

	// true positive
	private int matchedCounter;
	// false negative
	private int missedCounter;
	// false positive
	private int spuriousCounter;

	private int partiallyMatchedCounter;

	public void reportMissing(Type type, Annotation goldAnno) {
		// TODO Auto-generated method stub

	}

	public void reportMatching(Type type, Set<Annotation> goldAnnos, Set<Annotation> sysAnnos) {
		// TODO Auto-generated method stub
	}

	public void reportSpurious(Type type, Annotation sysAnno) {
		// TODO Auto-generated method stub

	}
}