/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class TypedPrintingEvaluationListener extends PrintingEvaluationListener {

	@Autowired
	protected TypeSystem ts;

	// config
	private String targetTypeName;
	protected boolean typeRequired = false;

	// derived
	protected Type targetType;

	@Override
	protected void init() throws Exception {
		// init type
		if (targetTypeName != null) {
			targetType = ts.getType(targetTypeName);
		}
		if (typeRequired && targetType == null) {
			throw new IllegalStateException("There is no type " + targetTypeName);
		}
		super.init();
	}

	public void setTargetTypeName(String targetTypeName) {
		this.targetTypeName = targetTypeName;
	}
}