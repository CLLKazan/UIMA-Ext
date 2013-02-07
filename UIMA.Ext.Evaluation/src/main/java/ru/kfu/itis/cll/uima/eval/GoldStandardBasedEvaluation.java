/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static com.google.common.collect.Maps.newTreeMap;
import static com.google.common.collect.Sets.newTreeSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.springframework.beans.factory.annotation.Autowired;

import ru.kfu.itis.cll.uima.eval.anno.AnnotationExtractor;
import ru.kfu.itis.cll.uima.eval.anno.DocumentMetaExtractor;
import ru.kfu.itis.cll.uima.eval.anno.MatchingStrategy;
import ru.kfu.itis.cll.uima.eval.cas.CasDirectory;

/**
 * Note! Complete annotation duplicates (by offsets & type) are ignored.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GoldStandardBasedEvaluation {

	@Resource(name = "systemCasDirectory")
	private CasDirectory systemOutputDir;
	@Resource(name = "goldCasDirectory")
	private CasDirectory goldStandardDir;
	@Autowired
	private AnnotationExtractor annotationExtractor;
	@Autowired
	private DocumentMetaExtractor docMetaExtractor;
	@Autowired
	private MatchingStrategy matchingStrategy;
	@Autowired
	private EvaluationContext evalCtx;

	public void run() throws Exception {
		Iterator<CAS> iter = goldStandardDir.iterator();
		while (iter.hasNext()) {
			CAS goldCas = iter.next();
			String docUri = docMetaExtractor.getDocumentUri(goldCas);
			CAS sysCas = systemOutputDir.getCas(docUri);
			if (sysCas == null) {
				throw new IllegalStateException("No CAS from system output for doc uri: " + docUri);
			}
			evalCtx.setCurrentDocUri(docUri);
			try {
				evaluate(goldCas, sysCas);
			} finally {
				// reset uri
				evalCtx.setCurrentDocUri(null);
			}
		}
		evalCtx.reportEvaluationComplete();
	}

	private void evaluate(CAS goldCas, CAS sysCas) {
		FSIterator<AnnotationFS> goldAnnoIter = annotationExtractor.extract(goldCas);
		Set<AnnotationFS> goldProcessed = new HashSet<AnnotationFS>();
		// system annotations that exactly match a gold one
		SortedSet<AnnotationFS> sysMatched = newTreeSet(AnnotationOffsetComparator.INSTANCE);
		// matches
		TreeMap<AnnotationFS, MatchInfo> matchesMap = newTreeMap(AnnotationOffsetComparator.INSTANCE);
		while (goldAnnoIter.hasNext()) {
			AnnotationFS goldAnno = goldAnnoIter.next();
			if (goldProcessed.contains(goldAnno)) {
				continue;
			}
			MatchInfo mi = new MatchInfo();
			matchesMap.put(goldAnno, mi);

			Set<AnnotationFS> candidatesRaw = matchingStrategy.searchCandidates(goldAnno, sysCas);
			Set<AnnotationFS> candidates = newTreeSet(AnnotationOffsetComparator.INSTANCE);
			candidates.addAll(candidatesRaw);

			candidates.removeAll(sysMatched);
			AnnotationFS exactSys = matchingStrategy.searchExactMatch(goldAnno, candidates);
			if (exactSys != null) {
				mi.exact = exactSys;
				sysMatched.add(exactSys);
			}
			mi.partialSet.addAll(candidates);

			goldProcessed.add(goldAnno);
		}

		// filter partials that match a next gold
		for (MatchInfo mi : matchesMap.values()) {
			mi.partialSet.removeAll(sysMatched);
		}

		// report for each gold anno
		for (AnnotationFS goldAnno : matchesMap.keySet()) {
			MatchInfo mi = matchesMap.get(goldAnno);
			if (mi.exact != null) {
				evalCtx.reportExactMatch(goldAnno, mi.exact);
			} else {
				evalCtx.reportMissing(goldAnno);
			}
			for (AnnotationFS partialSys : mi.partialSet) {
				evalCtx.reportPartialMatch(goldAnno, partialSys);
			}
		}

		// report spurious (false positives)
		FSIterator<AnnotationFS> sysAnnoIter = annotationExtractor.extract(sysCas);
		while (sysAnnoIter.hasNext()) {
			AnnotationFS sysAnno = sysAnnoIter.next();
			if (!sysMatched.contains(sysAnno)) {
				evalCtx.reportSpurious(sysAnno);
			}
		}
	}
}

class MatchInfo {
	AnnotationFS exact;
	TreeSet<AnnotationFS> partialSet = newTreeSet(AnnotationOffsetComparator.INSTANCE);
}