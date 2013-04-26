/**
 * 
 */
package org.nlplab.brat.configuration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class EventRole {
	public static enum Cardinality {
		ONE, OPTIONAL, ARRAY, NON_EMPTY_ARRAY
	}

	private String role;
	private BratType range;
	private Cardinality cardinality;

	public EventRole(String role, BratType range, Cardinality cardinality) {
		this.role = role;
		this.range = range;
		this.cardinality = cardinality;
		if (role == null || range == null || cardinality == null) {
			throw new NullPointerException();
		}
	}

	public String getRole() {
		return role;
	}

	public BratType getRange() {
		return range;
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
				.append(this.range, that.range)
				.append(this.cardinality, that.cardinality).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.NO_FIELD_NAMES_STYLE)
				.append(role).append(range).append(cardinality).toString();
	}
}