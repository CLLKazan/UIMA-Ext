/**
 * 
 */
package org.nlplab.brat.configuration;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.ImmutableSet;

public class EventRole {
	public static enum Cardinality {
		ONE, OPTIONAL, ARRAY, NON_EMPTY_ARRAY
	}

	private String role;
	private Set<BratType> rangeTypes;
	private Cardinality cardinality;

	public EventRole(String role, Iterable<BratType> rangeTypes, Cardinality cardinality) {
		this.role = role;
		this.rangeTypes = ImmutableSet.copyOf(rangeTypes);
		if (this.rangeTypes.isEmpty()) {
			throw new IllegalArgumentException("Empty rangeTypes");
		}
		this.cardinality = cardinality;
		if (role == null || rangeTypes == null || cardinality == null) {
			throw new NullPointerException();
		}
	}

	public EventRole(String role, BratType range, Cardinality cardinality) {
		this(role, ImmutableSet.of(range), cardinality);
	}

	public String getRole() {
		return role;
	}

	public Set<BratType> getRangeTypes() {
		return rangeTypes;
	}

	public Cardinality getCardinality() {
		return cardinality;
	}

	@Override
	public int hashCode() {
		return role.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EventRole)) {
			return false;
		}
		EventRole that = (EventRole) obj;
		return new EqualsBuilder().append(this.role, that.role)
				.append(this.rangeTypes, that.rangeTypes)
				.append(this.cardinality, that.cardinality).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.NO_FIELD_NAMES_STYLE)
				.append(role).append(rangeTypes).append(cardinality).toString();
	}
}