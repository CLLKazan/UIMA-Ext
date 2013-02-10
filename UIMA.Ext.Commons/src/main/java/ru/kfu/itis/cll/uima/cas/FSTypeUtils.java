/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

/**
 * Bunch of utility methods related to {@link Type} inspection.
 * 
 * @author Rinat Gareev
 * 
 */
public class FSTypeUtils {

	public static Feature getFeature(Type type, String featName, boolean mustExist) {
		Feature feature = type.getFeatureByBaseName(featName);
		if (feature == null && mustExist) {
			throw new IllegalArgumentException(String.format(
					"Feature %s#%s does not exist", type.getName(), featName));
		}
		return feature;
	}

	public static Type getType(TypeSystem ts, String typeName, boolean mustExist) {
		Type result = ts.getType(typeName);
		if (result == null && mustExist) {
			throw new IllegalArgumentException(String.format(
					"Type %s does not exist", typeName));
		}
		return result;
	}

	private FSTypeUtils() {
	}

}