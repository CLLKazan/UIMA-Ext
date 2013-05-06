/**
 * 
 */
package org.nlplab.brat.ann;

import java.util.Map;

import org.nlplab.brat.configuration.BratType;

import com.google.common.collect.ImmutableMap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratStructureAnnotation<BT extends BratType> extends BratAnnotation<BT> {

	private Map<String, BratAnnotation<?>> roleAnnotations;

	public BratStructureAnnotation(BT type, Map<String, ? extends BratAnnotation<?>> roleAnnotations) {
		super(type);
		setRoleAnnotations(roleAnnotations);
	}

	public Map<String, BratAnnotation<?>> getRoleAnnotations() {
		return roleAnnotations;
	}

	protected void setRoleAnnotations(Map<String, ? extends BratAnnotation<?>> roleAnnotations) {
		this.roleAnnotations = ImmutableMap.copyOf(roleAnnotations);
	}
}