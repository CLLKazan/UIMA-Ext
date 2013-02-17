/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.TypeSystem;

/**
 * @author Rinat Gareev
 * 
 */
public class FSTypeMatcher<FST extends FeatureStructure> implements Matcher<FST> {

	private boolean subtypeMatch;

	/**
	 * @param subtypesMatch
	 *            if true then the match is positive if ref type is the same or
	 *            superType of cand type. If false then only strict matching is
	 *            positive.
	 */
	public FSTypeMatcher(boolean subtypeMatch) {
		this.subtypeMatch = subtypeMatch;
	}

	@Override
	public boolean match(FeatureStructure ref, FeatureStructure cand) {
		if (!subtypeMatch) {
			return ref.getType().equals(cand.getType());
		} else {
			TypeSystem ts = ref.getCAS().getTypeSystem();
			return ts.subsumes(ref.getType(), cand.getType());
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(subtypeMatch).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FSTypeMatcher)) {
			return false;
		}
		FSTypeMatcher<?> that = (FSTypeMatcher<?>) obj;
		return this.subtypeMatch == that.subtypeMatch;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(ToStringStyle.SHORT_PREFIX_STYLE).append("subtypeMatch",
				subtypeMatch).toString();
	}

	@Override
	public void print(StringBuilder out, FST value) {
		out.append("type=").append(value.getType().getShortName());
	}
}