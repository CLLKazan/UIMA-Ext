/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import java.util.Map;
import java.util.Set;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationConfig {

	private String goldStandardImpl;
	private String systemOutputImpl;
	private Map<String, String> goldStandardProps;
	private Map<String, String> systemOutputProps;
	private Set<String> annoTypes;
	private String typeSystemDescPath;
	//
	private String docUriAnnotationType;
	private String docUriFeatureName;

	public Set<String> getAnnoTypes() {
		return annoTypes;
	}

	public String getGoldStandardImpl() {
		return goldStandardImpl;
	}

	public void setGoldStandardImpl(String goldStandardImpl) {
		this.goldStandardImpl = goldStandardImpl;
	}

	public String getSystemOutputImpl() {
		return systemOutputImpl;
	}

	public void setSystemOutputImpl(String systemOutputImpl) {
		this.systemOutputImpl = systemOutputImpl;
	}

	public void setAnnoTypes(Set<String> annoTypes) {
		this.annoTypes = annoTypes;
	}

	public Map<String, String> getGoldStandardProps() {
		return goldStandardProps;
	}

	public void setGoldStandardProps(Map<String, String> goldStandardProps) {
		this.goldStandardProps = goldStandardProps;
	}

	public Map<String, String> getSystemOutputProps() {
		return systemOutputProps;
	}

	public void setSystemOutputProps(Map<String, String> systemOutputProps) {
		this.systemOutputProps = systemOutputProps;
	}

	public String getTypeSystemDescPath() {
		return typeSystemDescPath;
	}

	public void setTypeSystemDescPath(String typeSystemDescPath) {
		this.typeSystemDescPath = typeSystemDescPath;
	}

	public String getDocUriAnnotationType() {
		return docUriAnnotationType;
	}

	public void setDocUriAnnotationType(String docUriAnnotationType) {
		this.docUriAnnotationType = docUriAnnotationType;
	}

	public String getDocUriFeatureName() {
		return docUriFeatureName;
	}

	public void setDocUriFeatureName(String docUriFeatureName) {
		this.docUriFeatureName = docUriFeatureName;
	}
}