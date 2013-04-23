/**
 * 
 */
package org.nlplab.brat.ann;

import java.util.Map;

import org.nlplab.brat.configuration.BratRelationType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratRelation extends BratStructureAnnotation<BratRelationType> {

	public BratRelation(BratRelationType type, BratEntity arg1, BratEntity arg2) {
		super(type, ImmutableMap.<String, BratEntity> of());
		checkArgVal(arg1);
		checkArgVal(arg2);
		Builder<String, BratEntity> b = ImmutableMap.builder();
		b.put(type.getArg1Name(), arg1);
		b.put(type.getArg2Name(), arg2);
		setRoleAnnotations(b.build());
	}

	/**
	 * auxiliary constructor
	 * 
	 * @param type
	 * @param arg1
	 * @param arg2
	 */
	public BratRelation(BratRelationType type, Map<String, BratEntity> argMap) {
		super(type, argMap);
		BratEntity arg1Val = argMap.get(type.getArg1Name());
		checkArgVal(arg1Val);
		BratEntity arg2Val = argMap.get(type.getArg2Name());
		checkArgVal(arg2Val);
	}

	public BratEntity getArg1() {
		return (BratEntity) getRoleAnnotations().get(getType().getArg1Name());
	}

	public BratEntity getArg2() {
		return (BratEntity) getRoleAnnotations().get(getType().getArg2Name());
	}

	private void checkArgVal(BratEntity anno) {
		if (anno == null) {
			throw new IllegalArgumentException(
					"Relation argument is NULL");
		}
	}
}