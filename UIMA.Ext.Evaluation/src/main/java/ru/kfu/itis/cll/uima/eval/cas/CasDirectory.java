/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.cas;

import java.util.Iterator;

import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface CasDirectory {
	JCas getCas(String docUri) throws Exception;

	Iterator<JCas> iterator();

	void setTypeSystem(TypeSystem ts);

	void init();
}