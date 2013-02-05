/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Arrays.asList;
import static org.apache.uima.util.CasCreationUtils.mergeTypeSystems;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.getOverlapping;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toList;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;

import ru.kfu.itis.cll.uima.cas.AnnotationUtils;
import ru.kfu.itis.cll.uima.eval.anno.AnnotationExtractor;
import ru.kfu.itis.cll.uima.eval.anno.DocumentMetaExtractor;
import ru.kfu.itis.cll.uima.eval.cas.CasDirectory;
import ru.kfu.itis.cll.uima.eval.cas.CasDirectoryFactory;

/**
 * Note! Complete annotation duplicates (by offsets & type) are ignored.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GoldStandardBasedEvaluation {

	// TODO inject
	private TypeSystem typeSystem;
	private CasDirectory systemOutputDir;
	private CasDirectory goldStandardDir;
	private AnnotationExtractor annotationExtractor;
	private DocumentMetaExtractor docMetaExtractor;

	// TODO replace configuration object passing by IOC injections 
	public GoldStandardBasedEvaluation(EvaluationConfig config) throws UIMAException, IOException {
		// TODO inject by IOC container
		this.systemOutputDir = CasDirectoryFactory.createDirectory(
				typeSystem, config.getSystemOutputImpl(), config.getSystemOutputProps());
		// TODO inject by IOC container
		this.goldStandardDir = CasDirectoryFactory.createDirectory(
				typeSystem, config.getGoldStandardImpl(), config.getGoldStandardProps());
	}

	public void run(EvaluationContext evalCtx) throws Exception {
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
				evaluate(evalCtx, goldCas, sysCas);
			} finally {
				// reset uri
				evalCtx.setCurrentDocUri(null);
			}
		}
		evalCtx.reportEvaluationComplete();
	}

	private void evaluate(EvaluationContext ctx, CAS goldCas, CAS sysCas) {
		FSIterator<AnnotationFS> goldAnnoIter = annotationExtractor.extract(goldCas);
		Set<AnnotationFS> goldProcessed = new HashSet<AnnotationFS>();
		SortedSet<AnnotationFS> sysProcessed = newTreeSet(AnnotationOffsetComparator.INSTANCE);
		while (goldAnnoIter.hasNext()) {
			AnnotationFS goldAnno = goldAnnoIter.next();
			if (goldProcessed.contains(goldAnno)) {
				continue;
			}

			JCasComposite goldClosure = new JCasComposite(goldCas);
			JCasComposite sysClosure = new JCasComposite(sysCas);
			fillClosure(goldAnno, goldClosure, sysClosure);

			if (sysClosure.annos.isEmpty()) {
				ctx.reportMissing(type, goldAnno);
			} else {
				ctx.reportMatching(type, goldClosure.annos, sysClosure.annos);
				sysProcessed.addAll(sysClosure.annos);
			}
			goldProcessed.addAll(goldClosure.annos);
		}

		// report spurious (false positive)
		FSIterator<AnnotationFS> sysAnnoIter = annotationExtractor.extract(sysCas);
		while (sysAnnoIter.hasNext()) {
			AnnotationFS sysAnno = sysAnnoIter.next();
			if (!sysProcessed.contains(sysAnno)) {
				ctx.reportSpurious(type, sysAnno);
			}
		}
	}

	private void fillClosure(AnnotationFS targetAnno, JCasComposite targetSide,
			JCasComposite otherSide) {
		targetSide.annos.add(targetAnno);

		List<Annotation> otherAnnos = toList(getOverlapping(
				otherSide.cas, annotationExtractor.extract(otherSide.cas), targetAnno));
		for (Annotation otherAnno : otherAnnos) {
			if (!otherSide.annos.contains(otherAnno)) {
				// swap sides
				fillClosure(otherAnno, otherSide, targetSide);
			}
		}
	}
}

class JCasComposite {
	SortedSet<AnnotationFS> annos;
	CAS cas;

	public JCasComposite(CAS cas) {
		this.cas = cas;
		this.annos = new TreeSet<AnnotationFS>(AnnotationOffsetComparator.INSTANCE);
	}
}