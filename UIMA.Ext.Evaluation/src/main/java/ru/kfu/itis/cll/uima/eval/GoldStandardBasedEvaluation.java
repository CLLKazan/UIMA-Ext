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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;

import ru.kfu.itis.cll.uima.cas.AnnotationUtils;
import ru.kfu.itis.cll.uima.eval.cas.CasDirectory;
import ru.kfu.itis.cll.uima.eval.cas.CasDirectoryFactory;

/**
 * Note! Complete annotation duplicates (by offsets & type) are ignored.
 * 
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
	private boolean stripDocumentUri;

	public GoldStandardBasedEvaluation(EvaluationConfig config) throws UIMAException, IOException {
		initTypeSystem(config);
		initTypes(config);
		this.systemOutputDir = CasDirectoryFactory.createDirectory(
				typeSystem, config.getSystemOutputImpl(), config.getSystemOutputProps());
		this.goldStandardDir = CasDirectoryFactory.createDirectory(
				typeSystem, config.getGoldStandardImpl(), config.getGoldStandardProps());
		this.stripDocumentUri = config.isStripDocumentUri();
	}

	private void initTypeSystem(EvaluationConfig config) throws IOException, UIMAException {
		TypeSystemDescription tsDesc = null;
		if (config.getTypeSystemDescPaths() != null) {
			tsDesc = createTypeSystemDescriptionFromPath(config.getTypeSystemDescPaths());
		}
		if (config.getTypeSystemDescNames() != null) {
			TypeSystemDescription tsDescFromNames = createTypeSystemDescription(
					config.getTypeSystemDescNames());
			if (tsDesc != null) {
				tsDesc = mergeTypeSystems(asList(tsDesc, tsDescFromNames));
			} else {
				tsDesc = tsDescFromNames;
			}
		}
		CAS dumbCas = CasCreationUtils.createCas(tsDesc, null, null);
		typeSystem = dumbCas.getTypeSystem();
		// printAllTypes();
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

	@SuppressWarnings("unused")
	private void printAllTypes() {
		Iterator<Type> iter = typeSystem.getTypeIterator();
		System.out.println("CAS types listing:");
		while (iter.hasNext()) {
			System.out.println(iter.next().getName());
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
			docUri = stripUri(docUri);
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

	private String stripUri(String srcUri) {
		if (!stripDocumentUri) {
			return srcUri;
		}
		try {
			URI uri = new URI(srcUri);
			String name = FilenameUtils.getName(uri.getPath());
			if (StringUtils.isBlank(name)) {
				name = srcUri;
			}
			return name;
		} catch (URISyntaxException e) {
			return srcUri;
		}
	}

	private void evaluate(EvaluationContext ctx, JCas goldCas, JCas sysCas) {
		for (Type curType : annoTypes) {
			evaluate(ctx, curType, goldCas, sysCas);
		}
	}

	private void evaluate(EvaluationContext ctx, Type type, JCas goldCas, JCas sysCas) {
		AnnotationIndex<Annotation> goldAnnoIndex = goldCas.getAnnotationIndex(type);
		AnnotationIndex<Annotation> sysAnnoIndex = sysCas.getAnnotationIndex(type);
		Set<Annotation> goldProcessed = new HashSet<Annotation>();
		SortedSet<Annotation> sysProcessed = newTreeSet(AnnotationOffsetComparator.INSTANCE);
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
				sysProcessed.addAll(sysClosure.annos);
			}
			goldProcessed.addAll(goldClosure.annos);
		}
		// report spurious (false positive)
		for (Annotation sysAnno : sysAnnoIndex) {
			if (!sysProcessed.contains(sysAnno)) {
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
		String uri = AnnotationUtils.getStringValue(cas, docMetaType, docUriFeature);
		if (uri == null) {
			throw new IllegalStateException("CAS doesn't have annotation of type " + docMetaType);
		}
		return uri;
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