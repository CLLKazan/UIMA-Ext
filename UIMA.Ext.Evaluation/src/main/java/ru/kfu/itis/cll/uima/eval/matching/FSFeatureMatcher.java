/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(feature).append(valueMatcher).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FSFeatureMatcher)) {
			return false;
		}
		FSFeatureMatcher<?, ?> that = (FSFeatureMatcher<?, ?>) obj;
		return new EqualsBuilder().append(this.feature, that.feature)
				.append(this.valueMatcher, that.valueMatcher)
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(ToStringStyle.SHORT_PREFIX_STYLE)
				.append("feature", feature)
				.append("valueMatcher", valueMatcher).toString();
	}

	@Override
	public void print(StringBuilder out, S value) {
		out.append(feature.getShortName()).append("=");
		E featValue = getValue(value);
		if (featValue == null) {
			out.append((Object) null);
		} else {
			valueMatcher.print(out, featValue);
		}
	}

	private E getValue(FeatureStructure fs) {
		E featureValue = (E) fs.getFeatureValue(feature);
		return featureValue;
	}
}