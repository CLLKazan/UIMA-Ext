/**
 * 
 */
package org.nlplab.brat.configuration;

import java.util.Collections;
import java.util.Set;

/**
 * @author Rinat Gareev
 * 
 */
public class BratRelationType extends BratType {

	private Set<BratEntityType> arg1Types;
	private Set<BratEntityType> arg2Types;

	public BratRelationType(String name,
			Set<BratEntityType> arg1Types,
			Set<BratEntityType> arg2Types) {
		super(name);
		this.arg1Types = Collections.unmodifiableSet(arg1Types);
		this.arg2Types = Collections.unmodifiableSet(arg2Types);
	}

	public Set<BratEntityType> getArg1Types() {
		return arg1Types;
	}

	public Set<BratEntityType> getArg2Types() {
		return arg2Types;
	}
}