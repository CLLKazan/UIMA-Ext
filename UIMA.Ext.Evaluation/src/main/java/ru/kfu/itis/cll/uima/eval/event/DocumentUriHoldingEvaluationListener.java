package ru.kfu.itis.cll.uima.eval.event;

/**
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class DocumentUriHoldingEvaluationListener implements EvaluationListener {

	protected String currentDocUri;

	@Override
	public void onDocumentChange(String docUri) {
		currentDocUri = docUri;
	}
}