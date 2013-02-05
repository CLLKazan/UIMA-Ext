/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

import ru.kfu.itis.cll.uima.cas.AnnotationUtils;

/**
 * @author Rinat Gareev
 * 
 */
public class DocumentMetaExtractor {

	// TODO inject
	private TypeSystem typeSystem;
	private String docUriAnnotationType;
	private String docUriFeatureName;
	// derived
	private Type docMetaType;
	private Feature docUriFeature;

	// TODO invoke
	private void init() {
		docMetaType = typeSystem.getType(docUriAnnotationType);
		if (docMetaType == null) {
			throw new IllegalStateException("Can't find annotation type '"
					+ docUriAnnotationType + "'");
		}
		docUriFeature = docMetaType.getFeatureByBaseName(docUriFeatureName);
		if (docUriFeature == null) {
			throw new IllegalStateException(String.format("No feature %s in type %s",
					docUriFeatureName, docMetaType));
		}
	}

	public String getDocumentUri(CAS cas) {
		String uri = AnnotationUtils.getStringValue(cas, docMetaType, docUriFeature);
		if (uri == null) {
			throw new IllegalStateException("CAS doesn't have annotation of type " + docMetaType);
		}
		return uri;
	}

}