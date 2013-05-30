/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import static ru.kfu.itis.cll.uima.eval.matching.MatchingUtils.isCollectionType;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
abstract class CollectionFeatureMatcherBase<FST extends FeatureStructure, E> extends
		MatcherBase<FST> {
	protected final Feature feature;
	protected final Matcher<E> elemMatcher;
	protected final boolean ignoreOrder;
	// delegate
	private final CollectionMatcher<E, Collection<E>> collectionMatcherDelegate;

	public CollectionFeatureMatcherBase(Feature feature, Matcher<E> elemMatcher, boolean ignoreOrder) {
		this.feature = feature;
		this.elemMatcher = elemMatcher;
		this.ignoreOrder = ignoreOrder;
		if (!isCollectionType(feature.getRange())) {
			throw new IllegalArgumentException(String.format(
					"Feature '%s' (of type '%s') range is not array", feature, feature.getDomain()));
		}
		collectionMatcherDelegate = new CollectionMatcher<E, Collection<E>>(
				elemMatcher, ignoreOrder);
	}

	@Override
	public boolean match(FST ref, FST cand) {
		Collection<E> refCol = getCollection(ref);
		Collection<E> candCol = getCollection(cand);
		return collectionMatcherDelegate.match(refCol, candCol);
	}

	protected abstract Collection<E> getCollection(FST srcFS);

	@Override
	protected String toString(IdentityHashMap<Matcher<?>, Integer> idMap) {
		idMap.put(this, getNextId(idMap));
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("feature", feature)
				.append("elemMatcher", getToString(idMap, elemMatcher))
				.append("ignoreOrder", ignoreOrder).toString();
	}

	@Override
	protected Collection<Matcher<?>> getSubMatchers() {
		List<Matcher<?>> result = Lists.newLinkedList();
		result.add(elemMatcher);
		return result;
	}

	@Override
	public void print(StringBuilder out, FST value) {
		Collection<E> col = getCollection(value);
		out.append(feature.getShortName());
		out.append("=");
		collectionMatcherDelegate.print(out, col);
	}
}
