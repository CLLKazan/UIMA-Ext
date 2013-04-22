/**
 * 
 */
package org.nlplab.brat.configuration;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Rinat Gareev
 * 
 */
public class BratEntityType extends BratType {

	private BratEntityType parentType;

	public BratEntityType(String name, BratEntityType parentType) {
		super(name);
		this.parentType = parentType;
	}

	public BratEntityType getParentType() {
		return parentType;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BratEntityType)) {
			return false;
		}
		BratEntityType that = (BratEntityType) obj;
		return new EqualsBuilder().append(this.name, that.name)
				.append(this.parentType, that.parentType).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("name", name).append("parentType", parentType).toString();
	}
}