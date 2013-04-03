/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import org.apache.uima.cas.text.AnnotationFS;

import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OverlapHelper {

	public static void evaluateOverlap(AnnotationFS gold, AnnotationFS sys,
			RecognitionMeasures measures) {
		// sanity check - one gold anno must give 1 score in total to the counters
		float totalBefore = measures.getTotatScore();

		// legend for schemas below: s - spurious, ! - matched, m - missing
		// Example 1.
		// golden ................sss|!!!!!!!.mmmm|.........
		// system ...............|...........|..............
		// Example 2.
		// golden ................sss|!!!!!!!|ssss..........
		// system ...............|................|.........
		float unionLength = Math.max(sys.getEnd(), gold.getEnd())
				- Math.min(sys.getBegin(), gold.getBegin());

		int deltaBefore = sys.getBegin() - gold.getBegin();
		if (deltaBefore > 0) {
			measures.incrementMissing(deltaBefore / unionLength);
		} else {
			measures.incrementSpurious(-deltaBefore / unionLength);
		}

		int deltaAfter = sys.getEnd() - gold.getEnd();
		if (deltaAfter > 0) {
			measures.incrementSpurious(deltaAfter / unionLength);
		} else {
			measures.incrementMissing(-deltaAfter / unionLength);
		}

		float overlapLength = Math.min(sys.getEnd(), gold.getEnd())
				- Math.max(sys.getBegin(), gold.getBegin());
		// sanity check
		if (overlapLength <= 0) {
			throw new IllegalStateException("Overlap length = " + overlapLength);
		}
		measures.incrementMatching(overlapLength / unionLength);

		// sanity check
		float totalAfter = measures.getTotatScore();
		if (totalAfter - totalBefore - 1 > 0.01f) {
			throw new IllegalStateException("Sanity check failed: totalAfter - totalBefore = "
					+ (totalAfter - totalBefore));
		}
	}

	private OverlapHelper() {
	}
}