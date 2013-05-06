/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.cas;

import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface CasDirectory {
	CAS getCas(String docUri) throws Exception;

	Iterator<CAS> iterator();

	void setTypeSystem(TypeSystem ts);

	void init();
}