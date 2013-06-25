/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.event;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.uima.cas.text.AnnotationFS;

import ru.kfu.itis.cll.uima.eval.measure.RecognitionMeasures;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class BestMatchEvaluatorBase extends TypedPrintingEvaluationListener {

	// config fields
	private OptimizationTarget optimizeBy = OptimizationTarget.F1;
	// state fields
	private RecognitionMeasures measures = new RecognitionMeasures();
	// per doc state
	private Map<AnnotationFS, AnnoEvalTuple> bestMatchesMap = Maps.newHashMap();
	private Set<AnnotationFS> sysHandled = Sets.newHashSet();

	protected abstract RecognitionMeasures evaluateAnno(AnnotationFS goldAnno, AnnotationFS sysAnno);

	public void setOptimizeBy(String optimizeByStr) {
		setOptimizeBy(Enum.valueOf(OptimizationTarget.class, optimizeByStr));
	}

	public void setOptimizeBy(OptimizationTarget optimizeBy) {
		this.optimizeBy = optimizeBy;
	}

	RecognitionMeasures getMeasures() {
		return measures;
	}

	@PostConstruct
	@Override
	protected void init() throws Exception {
		super.init();
	}

	@Override
	public void onDocumentChange(String docUri) {
		super.onDocumentChange(docUri);
		// report bestMatches, report other matches as spurious
		for (AnnotationFS goldAnno : bestMatchesMap.keySet()) {
			AnnoEvalTuple bestMatch = bestMatchesMap.get(goldAnno);
			RecognitionMeasures bmMeasures = normalize(bestMatch.measures);
			measures.incrementMatching(bmMeasures.getMatchedScore());
			measures.incrementMissing(bmMeasures.getMissedScore());
			measures.incrementSpurious(bmMeasures.getSpuriousScore());
			sysHandled.remove(bestMatch.anno);
		}
		measures.incrementSpurious(sysHandled.size());
		// clean per-doc state
		sysHandled.clear();
		bestMatchesMap.clear();
	}

	private RecognitionMeasures normalize(RecognitionMeasures src) {
		float total = src.getGoldScore();
		if (Math.abs(total - 1) < 0.001f) {
			// consider as already normalized
			return src;
		} else {
			RecognitionMeasures result = new RecognitionMeasures();
			result.incrementMatching(src.getMatchedScore() / total);
			result.incrementMissing(src.getMissedScore() / total);
			result.incrementSpurious(src.getSpuriousScore() / total);
			// sanity check
			if (Math.abs(result.getGoldScore() - 1) > 0.001f) {
				throw new IllegalStateException("Sanity check failed. Check code!");
			}
			return result;
		}
	}

	@Override
	public void onMissing(AnnotationFS goldAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		// 'missing' events are raised last per goldAnno
		if (!bestMatchesMap.containsKey(goldAnno)) {
			// there is no partials
			measures.incrementMissing(1);
		}
	}

	@Override
	public void onExactMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		RecognitionMeasures exactMatchMeasures = new RecognitionMeasures();
		exactMatchMeasures.incrementMatching(1);
		bestMatchesMap.put(goldAnno, new AnnoEvalTuple(sysAnno, exactMatchMeasures));
		sysHandled.add(sysAnno);
	}

	@Override
	public void onPartialMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		if (!checkType(goldAnno)) {
			return;
		}
		RecognitionMeasures sysAnnoMeasures = evaluateAnno(goldAnno, sysAnno);
		AnnoEvalTuple bestEvalTuple = bestMatchesMap.get(goldAnno);
		if (bestEvalTuple == null || compare(sysAnnoMeasures, bestEvalTuple.measures) > 0) {
			bestEvalTuple = new AnnoEvalTuple(sysAnno, sysAnnoMeasures);
			bestMatchesMap.put(goldAnno, bestEvalTuple);
		}
		sysHandled.add(sysAnno);
	}

	@Override
	public void onSpurious(AnnotationFS sysAnno) {
		if (!checkType(sysAnno)) {
			return;
		}
		if (!sysHandled.contains(sysAnno)) {
			measures.incrementSpurious(1);
		}
	}

	@Override
	public void onEvaluationComplete() {
		String report = MeasuresReportUtils.getReportString(targetType, measures);
		printer.println(report);
		clean();
	}

	private int compare(RecognitionMeasures x, RecognitionMeasures y) {
		Float xValue = optimizeBy.getValue(x);
		Float yValue = optimizeBy.getValue(y);
		return xValue.compareTo(yValue);
	}

	private boolean checkType(AnnotationFS anno) {
		if (targetType == null) {
			return true;
		}
		return ts.subsumes(targetType, anno.getType());
	}
}

class AnnoEvalTuple {
	final AnnotationFS anno;
	final RecognitionMeasures measures;

	public AnnoEvalTuple(AnnotationFS anno, RecognitionMeasures measures) {
		this.anno = anno;
		this.measures = measures;
	}
}

enum OptimizationTarget {

	F1 {
		@Override
		protected float getValue(RecognitionMeasures m) {
			return m.getF1();
		}
	},
	PRECISION {
		@Override
		protected float getValue(RecognitionMeasures m) {
			return m.getPrecision();
		}
	},
	RECALL {
		@Override
		protected float getValue(RecognitionMeasures m) {
			return m.getRecall();
		}
	};

	protected abstract float getValue(RecognitionMeasures m);
}