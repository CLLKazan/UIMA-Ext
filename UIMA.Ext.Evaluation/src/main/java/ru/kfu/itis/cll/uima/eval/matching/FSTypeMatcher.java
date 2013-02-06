/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.matching;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.TypeSystem;

/**
 * @author Rinat Gareev
 * 
 */
public class FSTypeMatcher implements Matcher<FeatureStructure> {

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
}