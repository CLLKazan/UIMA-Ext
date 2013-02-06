/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;

/**
 * @author Rinat Gareev
 * 
 */
public class FSFeatureMatcher<S extends FeatureStructure, E extends FeatureStructure> implements
		Matcher<S> {

	private Feature feature;
	private Matcher<E> valueMatcher;

	public FSFeatureMatcher(Feature feature, Matcher<E> valueMatcher) {
		this.feature = feature;
		this.valueMatcher = valueMatcher;
		// TODO what about FSList ?
		if (feature.getRange().isArray()) {
			throw new IllegalArgumentException(String.format(
					"Feature '%s' (of type '%s') range is array", feature, feature.getDomain()));
		}
	}

	@Override
	public boolean match(S ref, S cand) {
		E refValue = getValue(ref);
		E candValue = getValue(cand);
		if (refValue == null) {
			return candValue == null;
		}
		if (candValue == null) {
			return false;
		}
		return valueMatcher.match(refValue, candValue);
	}

	private E getValue(FeatureStructure fs) {
		E featureValue = (E) fs.getFeatureValue(feature);
		return featureValue;
	}
}