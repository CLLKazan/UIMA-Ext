/**
 * 
 */
package org.nlplab.brat.ann;

import org.nlplab.brat.configuration.BratType;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratStructureAnnotation<BT extends BratType> extends BratAnnotation<BT> {

	private Multimap<String, BratAnnotation<?>> roleAnnotations;

	public BratStructureAnnotation(BT type,
			Multimap<String, ? extends BratAnnotation<?>> roleAnnotations) {
		super(type);
		setRoleAnnotations(roleAnnotations);
	}

	public Multimap<String, BratAnnotation<?>> getRoleAnnotations() {
		return roleAnnotations;
	}

	protected void setRoleAnnotations(Multimap<String, ? extends BratAnnotation<?>> roleAnnotations) {
		this.roleAnnotations = ImmutableMultimap.copyOf(roleAnnotations);
	}
}