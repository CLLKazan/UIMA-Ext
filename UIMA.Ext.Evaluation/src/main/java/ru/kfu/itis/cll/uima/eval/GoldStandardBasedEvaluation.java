/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static java.lang.System.currentTimeMillis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ru.kfu.itis.cll.uima.eval.anno.AnnotationExtractor;
import ru.kfu.itis.cll.uima.eval.anno.DocumentMetaExtractor;
import ru.kfu.itis.cll.uima.eval.anno.MatchingStrategy;
import ru.kfu.itis.cll.uima.eval.cas.CasDirectory;

/**
 * TODO Complete annotation duplicates (by offsets & type) are ignored. Check
 * consistency.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GoldStandardBasedEvaluation {

	private final Logger log = LoggerFactory.getLogger(getClass());

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
		int processedCasCounter = 0;
		final int casDirSize = goldStandardDir.size();
		while (iter.hasNext()) {
			CAS goldCas = iter.next();
			final String docUri = docMetaExtractor.getDocumentUri(goldCas);
			CAS sysCas = systemOutputDir.getCas(docUri);
			if (sysCas == null) {
				throw new IllegalStateException("No CAS from system output for doc uri: " + docUri);
			}
			matchingStrategy.changeCas(sysCas);
			evalCtx.setCurrentDocUri(docUri);
			final long timeBeforeCas = currentTimeMillis();
			try {
				evaluate(goldCas, sysCas);
			} finally {
				// reset uri
				evalCtx.setCurrentDocUri(null);
				matchingStrategy.changeCas(null);
				processedCasCounter++;
				log.info("[{}/{}] {} has been processed in {}ms", new Object[] {
						processedCasCounter, casDirSize, docUri,
						currentTimeMillis() - timeBeforeCas });
			}
		}
		evalCtx.reportEvaluationComplete();
	}

	private void evaluate(CAS goldCas, CAS sysCas) {
		FSIterator<AnnotationFS> goldAnnoIter = annotationExtractor.extract(goldCas);
		Set<AnnotationFS> goldProcessed = new HashSet<AnnotationFS>();
		// system annotations that exactly match a gold one
		Set<AnnotationFS> sysMatched = newHashSet();
		// matches
		LinkedHashMap<AnnotationFS, MatchInfo> matchesMap = newLinkedHashMap();
		while (goldAnnoIter.hasNext()) {
			AnnotationFS goldAnno = goldAnnoIter.next();
			if (goldProcessed.contains(goldAnno)) {
				continue;
			}
			MatchInfo mi = new MatchInfo();
			matchesMap.put(goldAnno, mi);

			Set<AnnotationFS> candidates = newLinkedHashSet(
					matchingStrategy.searchCandidates(goldAnno));

			candidates.removeAll(sysMatched);
			AnnotationFS exactSys = matchingStrategy.searchExactMatch(goldAnno, candidates);
			if (exactSys != null) {
				// sanity check
				assert candidates.contains(exactSys);
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
			// assert order declared in EvaluationListener javadoc
			MatchInfo mi = matchesMap.get(goldAnno);
			boolean matchedExactly = mi.exact != null;
			if (matchedExactly) {
				evalCtx.reportExactMatch(goldAnno, mi.exact);
			}
			for (AnnotationFS partialSys : mi.partialSet) {
				evalCtx.reportPartialMatch(goldAnno, partialSys);
			}
			if (!matchedExactly) {
				evalCtx.reportMissing(goldAnno);
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
	Set<AnnotationFS> partialSet = newLinkedHashSet();
}