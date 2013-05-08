/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.nlplab.brat.ann.BratNoteAnnotation;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface BratNoteMapper {
	/**
	 * This is invoked during annotator (or collection reader) initialization.
	 * 
	 * @param ts
	 */
	void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException;

	/**
	 * This is invoked during UIMA-to-Brat mapping.
	 * 
	 * @param uAnno
	 * @return note content to save as {@link BratNoteAnnotation} for brat
	 *         annotation derived from given uAnno. Can return null - in this
	 *         case note annotation will not be created.
	 */
	String makeNote(AnnotationFS uAnno);

	/**
	 * This is invoked during Brat-to-UIMA mapping. Implementors should parse
	 * given noteContent to fill given uAnno features.
	 * 
	 * @param uAnno
	 * @param noteContent
	 */
	void parseNote(AnnotationFS uAnno, String noteContent);
}