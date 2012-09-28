/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AnnotatorUtils {

	public static void mandatoryParam(String paramName, Object value)
			throws ResourceInitializationException {
		if (value == null) {
			throw new ResourceInitializationException(
					new IllegalStateException(String.format(
							"Missing mandatory parameter '%s' value", paramName)));
		}
	}

	public static void mandatoryResourceObject(String resKey, Object resource)
			throws ResourceInitializationException {
		if (resource == null) {
			throw new ResourceInitializationException(
					new IllegalStateException(String.format(
							"Missing mandatory resource under '%s' key", resKey)));
		}
	}

	public static void annotationTypeExist(String typeName, Type type)
			throws AnalysisEngineProcessException {
		if (type == null) {
			throw new AnalysisEngineProcessException(
					new IllegalStateException(String.format(
							"Unknown type - '%s'", typeName)));
		}
	}

	public static Feature featureExist(Type type, String featureName)
			throws AnalysisEngineProcessException {
		Feature result = type.getFeatureByBaseName(featureName);
		if (result == null) {
			throw new AnalysisEngineProcessException(
					new IllegalStateException(String.format(
							"Type %s doesn't have feature '%s'", type, featureName)));
		}
		return result;
	}

	private AnnotatorUtils() {
	}

}