/**
 * 
 */
package org.nlplab.brat.configuration;

import java.util.Collections;
import java.util.Map;

/**
 * @author Rinat Gareev
 * 
 */
public class BratEventType extends BratType {

	private Map<String, EventSlot> slots;

	public BratEventType(String name, Map<String, EventSlot> slots) {
		super(name);
		this.slots = Collections.unmodifiableMap(slots);
	}

	public static enum Cardinality {
		ONE, OPTIONAL, ARRAY, NON_EMPTY_ARRAY
	}

	public static class EventSlot {
		private String role;
		private BratType range;
		private Cardinality cardinality;

		public EventSlot(String role, BratType range, Cardinality cardinality) {
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

	}

}