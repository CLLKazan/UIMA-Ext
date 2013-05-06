/**
 * 
 */
package ru.kfu.itis.cll.uima.cas;

import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.FeatureStructure;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class NotConstraint implements FSMatchConstraint {

	private static final long serialVersionUID = -2673253566146696649L;

	public static NotConstraint of(FSMatchConstraint argConstraint) {
		return new NotConstraint(argConstraint);
	}

	private FSMatchConstraint argConstraint;

	private NotConstraint(FSMatchConstraint argConstraint) {
		this.argConstraint = argConstraint;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean match(FeatureStructure fs) {
		return !argConstraint.match(fs);
	}
}