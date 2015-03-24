/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.util.FSCollectionFactory;

import static ru.kfu.itis.cll.uima.eval.matching.MatchingUtils.*;

/**
 * @author Rinat Gareev
 * 
 */
public class FSCollectionFeatureMatcher<FST extends FeatureStructure, E extends FeatureStructure>
		extends CollectionFeatureMatcherBase<FST, E> {

	public FSCollectionFeatureMatcher(Feature feature, Matcher<E> elemMatcher, boolean ignoreOrder) {
		super(feature, elemMatcher, ignoreOrder);
		Type elemType = getComponentType(feature.getRange());
		if (elemType.isPrimitive()) {
			throw new IllegalArgumentException(String.format(
					"Feature '%s' (of type '%s') range is array of primitives", feature,
					feature.getDomain()));
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(feature).append(elemMatcher).append(ignoreOrder)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FSCollectionFeatureMatcher)) {
			return false;
		}
		FSCollectionFeatureMatcher<?, ?> that = (FSCollectionFeatureMatcher<?, ?>) obj;
		return new EqualsBuilder().append(this.feature, that.feature)
				.append(this.elemMatcher, that.elemMatcher)
				.append(this.ignoreOrder, that.ignoreOrder).isEquals();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<E> getCollection(FeatureStructure anno) {
		ArrayFS fsArray = (ArrayFS) anno.getFeatureValue(feature);
		if (fsArray == null) {
			return null;
		}
		return (Collection<E>) FSCollectionFactory.create(fsArray);
	}
}