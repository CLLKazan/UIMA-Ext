/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.StringArrayFS;
import org.apache.uima.cas.Type;

import ru.kfu.itis.cll.uima.cas.FSUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class PrimitiveCollectionFeatureMatcher<FST extends FeatureStructure, E>
		extends CollectionFeatureMatcherBase<FST, E> {

	public static <FST extends FeatureStructure> Matcher<FST> forFeature(
			Feature feat, boolean ignoreOrder) {
		Type compType = MatchingUtils.getComponentType(feat.getRange());
		if (!compType.isPrimitive()) {
			throw new IllegalStateException(String.format(
					"Component type %s of feature %s is not primitive type",
					compType, feat));
		}
		if ("uima.cas.String".equals(compType.getName())) {
			return forStringCollection(feat, ignoreOrder);
		} else {
			// TODO LOW PRIORITY
			throw new UnsupportedOperationException(String.format(
					"PrimitiveCollectionFeatureMatcher for %s is not implemented yet", compType));
		}
	}

	private static <FST extends FeatureStructure> PrimitiveCollectionFeatureMatcher<FST, String> forStringCollection(
			final Feature feat, boolean ignoreOrder) {
		return new PrimitiveCollectionFeatureMatcher<FST, String>(feat, ignoreOrder) {
			@Override
			protected Collection<String> getCollection(FST srcFS) {
				return FSUtils.toList((StringArrayFS) srcFS.getFeatureValue(feat));
			}
		};
	}

	private PrimitiveCollectionFeatureMatcher(Feature feature,
			boolean ignoreOrder) {
		super(feature, EqualityMatcher.<E> getInstance(), ignoreOrder);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(ignoreOrder).append(elemMatcher).append(feature)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PrimitiveCollectionFeatureMatcher)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		PrimitiveCollectionFeatureMatcher<?, ?> that = (PrimitiveCollectionFeatureMatcher<?, ?>) obj;
		return new EqualsBuilder().append(this.ignoreOrder, that.ignoreOrder)
				.append(this.feature, that.feature)
				.append(this.elemMatcher, that.elemMatcher).isEquals();
	}
}