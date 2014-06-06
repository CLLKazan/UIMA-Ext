/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.eval;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;

import javax.annotation.PostConstruct;

import org.apache.uima.cas.FeatureStructure;
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
		FeatureStructure wf = casHelper.getWordform(anno);
		if (wf == null) {
			throw new IllegalStateException(String.format(
					"Word %s (%s) without wordforms",
					toPrettyString(anno), currentDocUri));
		}
		String tag = casHelper.getTag(wf);
		return String.valueOf(tag);
	}

	@PostConstruct
	@Override
	protected void init() throws Exception {
		super.init();
		casHelper = new MorphEvalHelper(ts);
	}

}
