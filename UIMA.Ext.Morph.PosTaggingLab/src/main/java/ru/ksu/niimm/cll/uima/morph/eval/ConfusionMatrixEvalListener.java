/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.eval;

import static java.util.Collections.reverseOrder;
import static java.util.Collections.sort;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;

import java.util.List;
import java.util.Set;

import org.apache.uima.cas.text.AnnotationFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kfu.itis.cll.uima.eval.event.PrintingEvaluationListener;
import ru.kfu.itis.cll.uima.util.Counter;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * TODO move to Eval module?
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class ConfusionMatrixEvalListener extends PrintingEvaluationListener {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	//
	private Table<String, String, Counter<Match>> matrix = HashBasedTable.create();
	private int partialMatchesIgnored;
	// per-CAS state
	private Set<AnnotationFS> goldAnnotationsProcessed = Sets.newHashSet();

	@Override
	public void onDocumentChange(String docUri) {
		super.onDocumentChange(docUri);
		goldAnnotationsProcessed.clear();
	}

	@Override
	public void onMissing(AnnotationFS goldAnno) {
	}

	@Override
	public void onExactMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		if (!goldAnnotationsProcessed.add(goldAnno)) {
			throw new IllegalStateException(String.format(
					"Unexpected exact match for %s in %s",
					toPrettyString(goldAnno), currentDocUri));
		}
		Counter<Match> counter = getCounter(goldAnno, sysAnno);
		counter.increment();
	}

	@Override
	public void onPartialMatch(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		if (!goldAnnotationsProcessed.add(goldAnno)) {
			log.warn("Gold annotation {} ({}) has been already added to the confusion matrix",
					toPrettyString(goldAnno), currentDocUri);
			partialMatchesIgnored++;
			return;
		}
		Counter<Match> counter = getCounter(goldAnno, sysAnno);
		counter.increment();
	}

	@Override
	public void onSpurious(AnnotationFS sysAnno) {
	}

	private Counter<Match> getCounter(AnnotationFS goldAnno, AnnotationFS sysAnno) {
		String goldLabel = getLabel(goldAnno);
		if (goldLabel == null) {
			throw new NullPointerException("goldLabel");
		}
		String sysLabel = getLabel(sysAnno);
		if (sysLabel == null) {
			throw new NullPointerException("sysLabel");
		}
		Counter<Match> counter = matrix.get(goldLabel, sysLabel);
		if (counter == null) {
			goldLabel = goldLabel.intern();
			sysLabel = sysLabel.intern();
			counter = Counter.create(new Match(goldLabel, sysLabel));
			matrix.put(goldLabel, sysLabel, counter);
		}
		return counter;
	}

	private static final String RECORD_FORMAT = "%s\t%s\t%s";

	@Override
	public void onEvaluationComplete() {
		List<Counter<Match>> counters = Lists.newArrayList(matrix.values());
		sort(counters, reverseOrder(Counter.valueComparator(Match.class)));
		// print results
		if (partialMatchesIgnored > 0) {
			printer.println("!!! PARTIAL MATCHES IGNORED: " + partialMatchesIgnored);
		}
		// header
		printer.println(String.format(RECORD_FORMAT, "Gold", "System", "Count"));
		// entries
		for (Counter<Match> c : counters) {
			Match m = c.getKey();
			String record = String.format(RECORD_FORMAT, m.gold, m.system, c.getValue());
			printer.println(record);
		}
		clean();
	}

	/**
	 * 
	 * @param anno
	 * @return class label for the given anno. Note that null values are not
	 *         allowed to return.
	 */
	protected abstract String getLabel(AnnotationFS anno);

	private static class Match {
		private final String gold;
		private final String system;

		public Match(String gold, String system) {
			this.gold = gold;
			this.system = system;
		}
	}
}