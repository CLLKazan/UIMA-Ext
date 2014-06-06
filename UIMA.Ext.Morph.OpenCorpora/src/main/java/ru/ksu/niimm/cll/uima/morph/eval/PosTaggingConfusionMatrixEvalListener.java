/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.eval;

import javax.annotation.PostConstruct;

import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTaggingConfusionMatrixEvalListener extends ConfusionMatrixEvalListener {

	@Autowired
	private TypeSystem ts;
	//
	private MorphEvalHelper casHelper;

	@Override
	protected String getLabel(AnnotationFS anno) {
		String tag = casHelper.getTag(anno);
		return String.valueOf(tag);
	}

	@PostConstruct
	@Override
	protected void init() throws Exception {
		super.init();
		casHelper = new MorphEvalHelper(ts);
	}

}
