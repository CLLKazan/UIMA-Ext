/**
 * 
 */
package org.nlplab.brat.configuration;

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
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.NO_FIELD_NAMES_STYLE)
				.append(role).append(range).append(cardinality).toString();
	}
}