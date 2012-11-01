package ru.kfu.itis.cll.uima.eval;

import java.io.PrintWriter;
import java.io.Writer;
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
	// derived
	private PrintWriter printer;

	// state fields
	// false negative
	private float missingCounter = 0;
	// false positive
	private float spuriousCounter = 0;
	// true positive
	private float matchingCounter = 0;
	//
	private int exactMatchingCounter;
	private int partialMatchingCounter;

	public SoftPrecisionRecallListener(String targetTypeName, Writer outputWriter) {
		this.targetTypeName = targetTypeName;
		printer = new PrintWriter(outputWriter, true);
	}

	public String getTargetTypeName() {
		return targetTypeName;
	}

	@Override
	public void onMissing(String docUri, Type type, Annotation goldAnno) {
		if (!type.getName().equals(targetTypeName)) {
			return;
		}
		missingCounter += 1;
	}

	@Override
	public void onMatching(String docUri, Type type,
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
				onMissing(docUri, type, gold);
			} else {
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
					missingCounter += deltaBefore / unionLength;
				} else {
					spuriousCounter += -deltaBefore / unionLength;
				}

				int deltaAfter = sys.getEnd() - gold.getEnd();
				if (deltaAfter > 0) {
					spuriousCounter += deltaAfter / unionLength;
				} else {
					missingCounter += -deltaAfter / unionLength;
				}

				if (deltaBefore == 0 && deltaAfter == 0) {
					exactMatchingCounter++;
				} else {
					partialMatchingCounter++;
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
			if (totalAfter - totalBefore - 1 > 0.01f) {
				throw new IllegalStateException("Sanity check failed: totalAfter - totalBefore = "
						+ (totalAfter - totalBefore));
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
	public void onSpurious(String docUri, Type type, Annotation sysAnno) {
		if (!type.getName().equals(targetTypeName)) {
			return;
		}
		spuriousCounter += 1;
	}

	@Override
	public void onEvaluationComplete() {
		StringBuilder sb = new StringBuilder();
		sb.append("Results for type '").append(getTargetTypeName()).append("':\n");
		if (getMatchedScore() == 0 && getSpuriousScore() == 0) {
			sb.append("System did not matched any annotation of this type");
		} else {
			sb.append("Matches score:   ").append(formatAsFloating(getMatchedScore()))
					.append("\n");
			sb.append("where:\n\tMatched exactly: ").append(exactMatchingCounter);
			sb.append("\n\tPartially matched: ").append(partialMatchingCounter).append("\n");
			sb.append("Misses score:    ").append(formatAsFloating(getMissedScore()))
					.append("\n");
			sb.append("Spurious score:  ").append(formatAsFloating(getSpuriousScore()))
					.append("\n");
			sb.append("Precision: ").append(formatAsPercentage(getPrecision()))
					.append("\n");
			sb.append("Recall:    ").append(formatAsPercentage(getRecall()))
					.append("\n");
			sb.append("F1:        ").append(formatAsPercentage(getF1()))
					.append("\n");
		}
		printer.println(sb.toString());
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

	private static String formatAsPercentage(float value) {
		return String.format("%.1f%%", value * 100);
	}

	private static String formatAsFloating(float value) {
		return String.format("%.2f", value);
	}
}