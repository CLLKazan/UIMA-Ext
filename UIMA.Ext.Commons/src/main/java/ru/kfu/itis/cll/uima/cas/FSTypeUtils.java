/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import java.util.LinkedHashSet;
import java.util.List;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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

	/**
	 * Example:
	 * <p>
	 * Result for type 'org.test.internal.Foobar':
	 * </p>
	 * <p>
	 * org, org.test, org.test.internal
	 * </p>
	 * 
	 * @param t
	 * @return namespaces of given type name ordered ordered from top-level to
	 *         the lowest.
	 */
	public static LinkedHashSet<String> getNamespaces(Type t) {
		if (t == null) {
			throw new NullPointerException("type");
		}
		LinkedHashSet<String> result = Sets.newLinkedHashSet();
		String[] nameSplit = t.getName().split("\\.");
		List<String> packageNames = Lists.newArrayList(nameSplit);
		// remove type short name
		packageNames.remove(packageNames.size() - 1);
		// generate namespaces
		Joiner nsJoiner = Joiner.on('.');
		for (int i = 0; i < packageNames.size(); i++) {
			String ns = nsJoiner.join(packageNames.subList(0, i + 1));
			result.add(ns);
		}
		assert result.size() == packageNames.size();
		return result;
	}

	private FSTypeUtils() {
	}

}