/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;

import com.google.common.base.Objects;

/**
 * @author Rinat Gareev
 * 
 */
public class PrimitiveFeatureMatcher implements Matcher<FeatureStructure> {

	private Feature feature;

	public PrimitiveFeatureMatcher(Feature feature) {
		this.feature = feature;
		if (!feature.getRange().isPrimitive()) {
			throw new IllegalArgumentException(String.format(
					"Feature %s.%s is not primitive", feature.getDomain(), feature));
		}
		// TODO
		if (feature.getRange().getName().equals("uima.cas.Float") ||
				feature.getRange().getName().equals("uima.cas.Double")) {
			throw new UnsupportedOperationException(
					"Floating point types matching is not implemented yet");
		}
	}

	@Override
	public boolean match(FeatureStructure ref, FeatureStructure cand) {
		String refValue = ref.getFeatureValueAsString(feature);
		String candValue = cand.getFeatureValueAsString(feature);
		return Objects.equal(refValue, candValue);
	}
}