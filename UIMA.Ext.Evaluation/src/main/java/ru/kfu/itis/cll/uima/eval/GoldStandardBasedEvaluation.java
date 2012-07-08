/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

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
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.XMLInputSource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GoldStandardBasedEvaluation {

	private TypeSystem typeSystem;
	private CasDirectory systemOutputDir;
	private CasDirectory goldStandardDir;
	private Set<Type> annoTypes;
	private Type docMetaType;
	private Feature docUriFeature;

	public GoldStandardBasedEvaluation(EvaluationConfig config) throws UIMAException, IOException {
		initTypeSystem(config);
		initTypes(config);
		this.systemOutputDir = CasDirectoryFactory.createDirectory(
				typeSystem, config.getSystemOutputImpl(), config.getSystemOutputProps());
		this.goldStandardDir = CasDirectoryFactory.createDirectory(
				typeSystem, config.getGoldStandardImpl(), config.getGoldStandardProps());
	}

	private void initTypeSystem(EvaluationConfig config) throws IOException, UIMAException {
		XMLInputSource tsDescInput = new XMLInputSource(config.getTypeSystemDescPath());
		TypeSystemDescription tsDesc = UIMAFramework.getXMLParser().parseTypeSystemDescription(
				tsDescInput);
		CAS dumbCas = CasCreationUtils.createCas(tsDesc, null, null);
		typeSystem = dumbCas.getTypeSystem();
		// init doc meta type
		docMetaType = typeSystem.getType(config.getDocUriAnnotationType());
		if (docMetaType == null) {
			throw new IllegalStateException("Can't find annotation type '"
					+ config.getDocUriAnnotationType() + "'");
		}
		String docUriFeatureName = config.getDocUriFeatureName();
		docUriFeature = docMetaType.getFeatureByBaseName(docUriFeatureName);
		if (docUriFeature == null) {
			throw new IllegalStateException(String.format("No feature %s in type %s",
					docUriFeatureName, docMetaType));
		}
	}

	private void initTypes(EvaluationConfig config) {
		Set<String> annoTypeNames = config.getAnnoTypes();
		annoTypes = new HashSet<Type>();
		for (String curTypeName : annoTypeNames) {
			Type curType = typeSystem.getType(curTypeName);
			if (curType == null) {
				throw new IllegalStateException("Can't find type " + curTypeName);
			}
			annoTypes.add(curType);
		}
	}

	public void run(EvaluationContext evalCtx) throws Exception {
		Iterator<JCas> iter = goldStandardDir.iterator();
		while (iter.hasNext()) {
			JCas goldCas = iter.next();
			String docUri = getDocUri(goldCas);
			JCas sysCas = systemOutputDir.getCas(docUri);
			if (sysCas == null) {
				throw new IllegalStateException("No CAS from system output for doc uri: " + docUri);
			}
			evaluate(evalCtx, goldCas, sysCas);
		}
	}

	private void evaluate(EvaluationContext ctx, JCas goldCas, JCas sysCas) {
		for (Type curType : annoTypes) {
			evaluate(ctx, curType, goldCas, sysCas);
		}
	}

	private void evaluate(EvaluationContext ctx, Type type, JCas goldCas, JCas sysCas) {
		Set<Annotation> evaluatedAnnos = new HashSet<Annotation>();
		AnnotationIndex<Annotation> goldAnnoIndex = goldCas.getAnnotationIndex(type);
		AnnotationIndex<Annotation> sysAnnoIndex = sysCas.getAnnotationIndex(type);
		Set<Annotation> goldProcessed = new HashSet<Annotation>();
		for (Annotation goldAnno : goldAnnoIndex) {
			if (goldProcessed.contains(goldAnno)) {
				continue;
			}

			JCasComposite goldClosure = new JCasComposite(goldCas, goldAnnoIndex);
			JCasComposite sysClosure = new JCasComposite(sysCas, sysAnnoIndex);
			fillClosure(goldAnno, goldClosure, sysClosure);

			if (sysClosure.annos.isEmpty()) {
				ctx.reportMissing(type, goldAnno);
			} else {
				ctx.reportMatching(type, goldClosure.annos, sysClosure.annos);
				evaluatedAnnos.addAll(sysClosure.annos);
			}
			goldProcessed.addAll(goldClosure.annos);
		}
		// report spurious (false positive)
		for (Annotation sysAnno : sysAnnoIndex) {
			if (!evaluatedAnnos.contains(sysAnno)) {
				ctx.reportSpurious(type, sysAnno);
			}
		}
	}

	private void fillClosure(Annotation targetAnno, JCasComposite targetSide,
			JCasComposite otherSide) {
		targetSide.annos.add(targetAnno);

		List<Annotation> otherAnnos = toList(getOverlapping(
				otherSide.cas, otherSide.annoIndex.iterator(), targetAnno));
		for (Annotation otherAnno : otherAnnos) {
			if (!otherSide.annos.contains(otherAnno)) {
				// swap sides
				fillClosure(otherAnno, otherSide, targetSide);
			}
		}
	}

	private String getDocUri(JCas cas) {
		AnnotationIndex<Annotation> metaIdx = cas.getAnnotationIndex(docMetaType);
		if (metaIdx.size() > 0) {
			Annotation meta = metaIdx.iterator().next();
			return meta.getFeatureValueAsString(docUriFeature);
		} else {
			throw new IllegalStateException("CAS without meta annotation typed " + docMetaType);
		}
	}
}

class JCasComposite {
	SortedSet<Annotation> annos;
	JCas cas;
	AnnotationIndex<Annotation> annoIndex;

	public JCasComposite(JCas cas, AnnotationIndex<Annotation> annoIndex) {
		this.cas = cas;
		this.annoIndex = annoIndex;
		this.annos = new TreeSet<Annotation>(AnnotationOffsetComparator.INSTANCE);
	}
}