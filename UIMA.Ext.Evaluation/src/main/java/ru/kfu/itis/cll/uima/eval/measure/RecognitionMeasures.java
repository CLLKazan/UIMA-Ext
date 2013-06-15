/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.measure;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class RecognitionMeasures {

	// state fields
	// false negative
	private float missingCounter = 0;
	// false positive
	private float spuriousCounter = 0;
	// true positive
	private float matchingCounter = 0;

	public void incrementMissing(float delta) {
		missingCounter += delta;
	}

	public void incrementSpurious(float delta) {
		spuriousCounter += delta;
	}

	public void incrementMatching(float delta) {
		matchingCounter += delta;
	}

	public float getPrecision() {
		float recognizedScore = matchingCounter + spuriousCounter;
		return matchingCounter / recognizedScore;
	}

	public float getRecall() {
		return matchingCounter / (matchingCounter + missingCounter);
	}

	public float getF1() {
		float precision = getPrecision();
		float recall = getRecall();
		return 2 * precision * recall / (precision + recall);
	}

	public float getMatchedScore() {
		return matchingCounter;
	}

	public float getSpuriousScore() {
		return spuriousCounter;
	}

	public float getMissedScore() {
		return missingCounter;
	}

	/**
	 * @return matchingScore + missingScore
	 */
	public float getGoldScore() {
		return matchingCounter + missingCounter;
	}
}