/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.uima.cas.ArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.uimafit.util.FSCollectionFactory;

/**
 * @author Rinat Gareev
 * 
 */
public class FSCollectionFeatureMatcher<FST extends FeatureStructure, E extends FeatureStructure>
		implements Matcher<FST> {

	private Feature feature;
	private Matcher<E> elemMatcher;
	private boolean ignoreOrder;

	public FSCollectionFeatureMatcher(Feature feature, Matcher<E> elemMatcher, boolean ignoreOrder) {
		this.feature = feature;
		this.elemMatcher = elemMatcher;
		this.ignoreOrder = ignoreOrder;
		// TODO what about FSList ?
		if (!feature.getRange().isArray()) {
			throw new IllegalArgumentException(String.format(
					"Feature '%s' (of type '%s') range is not array", feature, feature.getDomain()));
		}
		Type elemType = feature.getRange().getComponentType();
		if (elemType.isPrimitive()) {
			throw new IllegalArgumentException(String.format(
					"Feature '%s' (of type '%s') range is array of primitives", feature,
					feature.getDomain()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(FST ref, FST cand) {
		Collection<E> refCol = getCollection(ref);
		Collection<E> candCol = getCollection(cand);
		if (refCol == null) {
			return candCol == null;
		}
		if (candCol == null) {
			return false;
		}
		if (refCol.size() != candCol.size()) {
			return false;
		}
		if (!ignoreOrder) {
			Iterator<E> candIter = candCol.iterator();
			for (E refElem : refCol) {
				E candElem = candIter.next();
				if (!elemMatcher.match(refElem, candElem)) {
					return false;
				}
			}
		} else {
			// order should be ignored
			List<E> nonMatchedCandElems = newArrayList(candCol);
			for (E refElem : refCol) {
				int matchedCandIndex = search(refElem, nonMatchedCandElems);
				if (matchedCandIndex < 0) {
					return false;
				}
				nonMatchedCandElems.remove(matchedCandIndex);
			}
		}
		return true;
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
	public String toString() {
		return new ToStringBuilder(ToStringStyle.SHORT_PREFIX_STYLE)
				.append("feature", feature)
				.append("elemMatcher", elemMatcher)
				.append("ignoreOrder", ignoreOrder).toString();
	}

	@Override
	public void print(StringBuilder out, FST value) {
		Collection<E> col = getCollection(value);
		out.append(feature.getShortName());
		out.append("=");

		if (col == null) {
			out.append((Object) null);
		} else {
			out.append("{");
			Iterator<E> iter = col.iterator();
			if (iter.hasNext()) {
				elemMatcher.print(out, iter.next());
			}
			while (iter.hasNext()) {
				out.append(",");
				elemMatcher.print(out, iter.next());
			}
			out.append("}");
		}
	}

	private int search(E refElem, List<E> candidatesList) {
		for (int i = 0; i < candidatesList.size(); i++) {
			E cand = candidatesList.get(i);
			if (elemMatcher.match(refElem, cand)) {
				return i;
			}
		}
		return -1;
	}

	private Collection<E> getCollection(FeatureStructure anno) {
		ArrayFS fsArray = (ArrayFS) anno.getFeatureValue(feature);
		if (fsArray == null) {
			return null;
		}
		return (Collection<E>) FSCollectionFactory.create(fsArray);
	}
}