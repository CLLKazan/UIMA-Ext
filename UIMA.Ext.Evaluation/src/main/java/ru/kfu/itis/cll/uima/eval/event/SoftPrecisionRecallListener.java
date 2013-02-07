package ru.kfu.itis.cll.uima.eval.event;

import java.util.Set;

import org.apache.uima.cas.text.AnnotationFS;

import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

import com.google.common.collect.Sets;

/**
 * TODO rename to softPRListener based on overlap length
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SoftPrecisionRecallListener extends TypedPrintingEvaluationListener {

	// state fields
	private RecognitionMeasures measures;
	//
	private int exactMatchingCounter;
	private int partialMatchingCounter;
	// per document state
	private Set<AnnotationFS> partiallyMatched;

	public SoftPrecisionRecallListener() {
		typeRequired = true;
	}

	@Override
	public void onDocumentChange(String docUri) {
		partiallyMatched = Sets.newHashSet();
	}

	@Override
	public void onMissing(AnnotationFS goldAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		measures.incrementMissing(1);
	}

	@Override
	public void onExactMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		onMatch(goldAnno, sysAnno);
	}

	@Override
	public void onPartialMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		if (!partiallyMatched.contains(sysAnno)) {
			onMatch(goldAnno, sysAnno);
			partiallyMatched.add(sysAnno);
		}
	}

	private void onMatch(AnnotationFS gold, AnnotationFS sys) {
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
		measures.incrementMatching(overlapLength / unionLength);

		// sanity check
		float totalAfter = measures.getTotatScore();
		if (totalAfter - totalBefore - 1 > 0.01f) {
			throw new IllegalStateException("Sanity check failed: totalAfter - totalBefore = "
					+ (totalAfter - totalBefore));
		}
	}

	@Override
	public void onSpurious(AnnotationFS sysAnno) {
		if (!checkType(sysAnno)) {
			return;
		}
		measures.incrementSpurious(1);
	}

	@Override
	public void onEvaluationComplete() {
		StringBuilder sb = new StringBuilder();
		sb.append("Results for type '").append(targetType.getName()).append("':\n");
		if (measures.getMatchedScore() == 0 && measures.getSpuriousScore() == 0) {
			sb.append("System did not matched any annotation of this type");
		} else {
			sb.append("Matches score:   ").append(formatAsFloating(
					measures.getMatchedScore())).append("\n");
			sb.append("where:\n\tMatched exactly: ").append(exactMatchingCounter);
			sb.append("\n\tPartially matched: ").append(partialMatchingCounter).append("\n");
			sb.append("Misses score:    ").append(formatAsFloating(
					measures.getMissedScore())).append("\n");
			sb.append("Spurious score:  ").append(formatAsFloating(
					measures.getSpuriousScore())).append("\n");
			sb.append("Precision: ").append(formatAsPercentage(
					measures.getPrecision())).append("\n");
			sb.append("Recall:    ").append(formatAsPercentage(
					measures.getRecall())).append("\n");
			sb.append("F1:        ").append(formatAsPercentage(
					measures.getF1())).append("\n");
		}
		printer.println(sb.toString());
	}

	public RecognitionMeasures getMeasures() {
		return measures;
	}

	private boolean checkType(AnnotationFS anno) {
		return ts.subsumes(targetType, anno.getType());
	}

	private static String formatAsPercentage(float value) {
		return String.format("%.1f%%", value * 100);
	}

	private static String formatAsFloating(float value) {
		return String.format("%.2f", value);
	}
}