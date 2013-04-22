/**
 * 
 */
package org.nlplab.brat.configuration;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.ImmutableSet;

/**
 * @author Rinat Gareev
 * 
 */
public class BratRelationType extends BratType {

	private String arg1Name;
	private Set<BratEntityType> arg1Types;
	private String arg2Name;
	private Set<BratEntityType> arg2Types;

	public BratRelationType(String name,
			Set<BratEntityType> arg1Types,
			String arg1Name,
			Set<BratEntityType> arg2Types,
			String arg2Name) {
		super(name);
		this.arg1Name = arg1Name;
		this.arg1Types = ImmutableSet.copyOf(arg1Types);
		this.arg2Name = arg2Name;
		this.arg2Types = ImmutableSet.copyOf(arg2Types);
	}

	public Set<BratEntityType> getArg1Types() {
		return arg1Types;
	}

	public Set<BratEntityType> getArg2Types() {
		return arg2Types;
	}

	public String getArg1Name() {
		return arg1Name;
	}

	public String getArg2Name() {
		return arg2Name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BratRelationType)) {
			return false;
		}
		BratRelationType that = (BratRelationType) obj;
		return new EqualsBuilder().append(name, that.name)
				.append(arg1Types, that.arg1Types)
				.append(arg2Types, that.arg2Types)
				.append(arg1Name, that.arg1Name)
				.append(arg2Name, that.arg2Name).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("name", name)
				.append("arg1Name", arg1Name).append("arg1Types", arg1Types)
				.append("arg2Name", arg2Name).append("arg2Types", arg2Types)
				.toString();
	}
}