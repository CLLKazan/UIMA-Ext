package ru.kfu.itis.cll.uima.eval;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.tcas.Annotation;

import ru.kfu.itis.cll.uima.cas.AnnotationUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SoftPrecisionRecallListener implements EvaluationListener {

	// config
	private String targetTypeName;

	// state fields
	// false negative
	private float missingCounter = 0;
	// false positive
	private float spuriousCounter = 0;
	// true positive
	private float matchingCounter = 0;

	public SoftPrecisionRecallListener(String targetTypeName) {
		this.targetTypeName = targetTypeName;
	}

	public String getTargetTypeName() {
		return targetTypeName;
	}

	@Override
	public void onMissing(Type type, Annotation goldAnno) {
		if (!type.getName().equals(targetTypeName)) {
			return;
		}
		missingCounter += 1;
	}

	@Override
	public void onMatching(Type type,
			SortedSet<Annotation> goldAnnos, SortedSet<Annotation> sysAnnos) {
		if (!type.getName().equals(targetTypeName)) {
			return;
		}
		LinkedList<Annotation> goldList = new LinkedList<Annotation>(goldAnnos);
		LinkedList<Annotation> sysList = new LinkedList<Annotation>(sysAnnos);
		while (!goldList.isEmpty()) {
			// sanity check - one gold anno must give 1 score in total to the counters
			float totalBefore = spuriousCounter + missingCounter + matchingCounter;

			Annotation gold = goldList.getFirst();
			Annotation sys = getMostOverlapping(gold, sysList);
			if (sys == null) {
				onMissing(type, gold);
			} else {
				// legend for schemas below: s - spurious, ! - matched, m - missing
				// Example 1.
				// golden ................sss|!!!!!!!.mmmm|.........
				// system ...............|...........|..............
				// Example 2.
				// golden ................sss|!!!!!!!|ssss..........
				// system ...............|................|.........
				int unionLength = Math.max(sys.getEnd(), gold.getEnd())
						- Math.min(sys.getBegin(), gold.getBegin());

				float deltaBefore = sys.getBegin() - gold.getBegin();
				if (deltaBefore > 0) {
					missingCounter += deltaBefore / unionLength;
				} else {
					spuriousCounter += -deltaBefore / unionLength;
				}

				float deltaAfter = sys.getEnd() - gold.getEnd();
				if (deltaAfter > 0) {
					spuriousCounter += deltaAfter / unionLength;
				} else {
					missingCounter += -deltaAfter / unionLength;
				}

				float overlapLength = Math.min(sys.getEnd(), gold.getEnd())
						- Math.max(sys.getBegin(), gold.getBegin());
				// sanity check
				if (overlapLength <= 0) {
					throw new IllegalStateException("Overlap length = " + overlapLength);
				}
				matchingCounter += overlapLength / unionLength;

				sysList.remove(sys);
			}
			goldList.remove(gold);

			// sanity check
			float totalAfter = spuriousCounter + missingCounter + matchingCounter;
			if (totalAfter - totalBefore != 1) {
				throw new IllegalStateException("Sanity check failed");
			}
		}
		// handle all remaining annotation in sysList as spurious
		spuriousCounter += sysList.size();
	}

	private Annotation getMostOverlapping(Annotation target, List<Annotation> srcList) {
		Annotation result = null;
		int maxOverlap = 0;
		for (Annotation candidate : srcList) {
			int candidateOverlap = AnnotationUtils.overlapLength(target, candidate);
			// if candidateOverlap == maxOverlap then previous (in the left) overlapping has priority
			if (candidateOverlap > maxOverlap) {
				maxOverlap = candidateOverlap;
				result = candidate;
			}
		}
		return result;
	}

	@Override
	public void onSpurious(Type type, Annotation sysAnno) {
		if (!type.getName().equals(targetTypeName)) {
			return;
		}
		spuriousCounter += 1;
	}

	public float getPrecision() {
		return matchingCounter / (matchingCounter + spuriousCounter);
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
}