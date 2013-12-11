/**
 * 
 */
package org.nlplab.brat.configuration;

import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.ImmutableMap;

/**
 * @author Rinat Gareev
 * 
 */
public class BratEventType extends BratType implements HasRoles {

	private Map<String, EventRole> roles;

	public BratEventType(String name, Map<String, EventRole> roles) {
		super(name);
		this.roles = ImmutableMap.copyOf(roles);
	}

	@Override
	public boolean isLegalAssignment(String roleName, BratType t) {
		if (!hasRole(roleName)) {
			throw new IllegalArgumentException(String.format(
					"Unknown role '%s' in %s", roleName, this));
		}
		EventRole role = roles.get(roleName);
		for (BratType roleType : role.getRangeTypes()) {
			// TODO check type hierarchy, not just a name
			if (roleType.getName().equals(t.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasRole(String role) {
		return roles.containsKey(role);
	}

	public EventRole getRole(String roleName) {
		EventRole eventRole = roles.get(roleName);
		if (eventRole == null) {
			throw new IllegalArgumentException(String.format(
					"No role '%s' in %s", roleName, this));
		}
		return eventRole;
	}

	public Map<String, EventRole> getRoles() {
		return roles;
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
		if (!(obj instanceof BratEventType)) {
			return false;
		}
		BratEventType that = (BratEventType) obj;
		return new EqualsBuilder().append(name, that.name)
				.append(roles, that.roles).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("name", name).append("roles", roles).toString();
	}
}