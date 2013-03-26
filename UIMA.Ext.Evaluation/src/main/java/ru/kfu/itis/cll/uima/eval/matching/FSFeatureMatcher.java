/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;

import com.google.common.collect.Lists;

import static ru.kfu.itis.cll.uima.eval.matching.MatchingUtils.*;

/**
 * @author Rinat Gareev
 * 
 */
public class FSFeatureMatcher<S extends FeatureStructure, E extends FeatureStructure>
		extends MatcherBase<S> {

	private Feature feature;
	private Matcher<E> valueMatcher;

	public FSFeatureMatcher(Feature feature, Matcher<E> valueMatcher) {
		this.feature = feature;
		this.valueMatcher = valueMatcher;
		if (isCollectionType(feature.getRange())) {
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
	protected String toString(IdentityHashMap<Matcher<?>, Integer> idMap) {
		idMap.put(this, getNextId(idMap));
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("feature", feature)
				.append("valueMatcher", getToString(idMap, valueMatcher)).toString();
	}

	@Override
	protected Collection<Matcher<?>> getSubMatchers() {
		List<Matcher<?>> result = Lists.newLinkedList();
		result.add(valueMatcher);
		return result;
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

	@SuppressWarnings("unchecked")
	private E getValue(FeatureStructure fs) {
		E featureValue = (E) fs.getFeatureValue(feature);
		return featureValue;
	}
}