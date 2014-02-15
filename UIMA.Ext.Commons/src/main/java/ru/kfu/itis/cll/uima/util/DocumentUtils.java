/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.featureExist;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

/**
 * @author Rinat Gareev
 * 
 */
public class DocumentUtils {

	/**
	 * search for a {@link DocumentMetadata} annotation in given CAS and return
	 * its 'sourceUri' feature value
	 * 
	 * @param cas
	 * @return sourceUri value or null if there is no a DocumentMetadata
	 *         annotation
	 */
	public static String getDocumentUri(CAS cas) {
		TypeSystem ts = cas.getTypeSystem();
		Type docMetaType = ts.getType(DocumentMetadata.class.getName());
		if (docMetaType == null) {
			return null;
		}
		Feature sourceUriFeat;
		try {
			sourceUriFeat = featureExist(docMetaType, "sourceUri");
		} catch (AnalysisEngineProcessException e) {
			throw new IllegalStateException(e);
		}
		FSIterator<AnnotationFS> dmIter = cas.getAnnotationIndex(docMetaType).iterator();
		if (dmIter.hasNext()) {
			AnnotationFS docMeta = dmIter.next();
			return docMeta.getStringValue(sourceUriFeat);
		} else {
			return null;
		}
	}

	public static String getDocumentUri(JCas jcas) {
		// TODO is there more optimal solution?
		return getDocumentUri(jcas.getCas());
	}

	public static String getFilename(String uriStr) throws URISyntaxException {
		URI uri = new URI(uriStr);
		return FilenameUtils.getName(uri.getPath());
	}

	public static String getFilenameSafe(String uriStr) {
		String name;
		try {
			name = getFilename(uriStr);
			if (StringUtils.isBlank(name)) {
				name = uriStr;
			}
		} catch (URISyntaxException e) {
			name = uriStr;
		}
		return name;
	}

	private DocumentUtils() {
	}
}