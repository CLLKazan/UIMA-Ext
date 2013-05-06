/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.anno;

import javax.annotation.PostConstruct;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ru.kfu.itis.cll.uima.cas.AnnotationUtils;

/**
 * @author Rinat Gareev
 * 
 */
public class DocumentMetaExtractor {

	@Autowired
	private TypeSystem typeSystem;
	@Value("${document.meta.annotationType}")
	private String docUriAnnotationType;
	@Value("${document.meta.uriFeatureName}")
	private String docUriFeatureName;
	// derived
	private Type docMetaType;
	private Feature docUriFeature;

	@SuppressWarnings("unused")
	@PostConstruct
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