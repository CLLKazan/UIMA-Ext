/**
 * 
 */
package org.nlplab.brat.ann;

import java.util.Map;

import org.nlplab.brat.configuration.BratRelationType;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratRelation extends BratAnnotation<BratRelationType> {

	private BratEntity arg1;
	private BratEntity arg2;

	public BratRelation(BratRelationType type, BratEntity arg1, BratEntity arg2) {
		super(type);
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	/**
	 * auxiliary constructor
	 * 
	 * @param type
	 * @param arg1
	 * @param arg2
	 */
	public BratRelation(BratRelationType type, Map<String, BratEntity> argMap) {
		super(type);
		BratEntity arg1Val = argMap.get(type.getArg1Name());
		checkArgVal(arg1Val);
		this.arg1 = arg1Val;
		BratEntity arg2Val = argMap.get(type.getArg2Name());
		checkArgVal(arg2Val);
		this.arg2 = arg2Val;
	}

	private void checkArgVal(BratEntity anno) {
		if (anno == null) {
			throw new IllegalArgumentException(
					"Relation argument is NULL");
		}
	}
}