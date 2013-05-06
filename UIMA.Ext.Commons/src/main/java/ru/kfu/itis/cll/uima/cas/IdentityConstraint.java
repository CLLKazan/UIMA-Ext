/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.FeatureStructure;

import com.google.common.base.Objects;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class IdentityConstraint implements FSMatchConstraint {

	private static final long serialVersionUID = -2827846978417612056L;

	public static IdentityConstraint of(FeatureStructure fs) {
		return new IdentityConstraint(fs);
	}

	private FeatureStructure fs;

	private IdentityConstraint(FeatureStructure fs) {
		this.fs = fs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(FeatureStructure fs) {
		return Objects.equal(this.fs, fs);
	}

}