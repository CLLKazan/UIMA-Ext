/**
 * 
 */
package ru.kfu.itis.cll.uima.annotator;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.coveredTextFunction;
import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.AnalysisEngineFactory;

import ru.kfu.itis.cll.uima.cas.AnnotationUtils;
import ru.kfu.itis.cll.uima.cas.OverlapIndex;

import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class NestedAnnotationRemover extends CasAnnotator_ImplBase {

	public static AnalysisEngineDescription createDescription(String targetTypeName)
			throws ResourceInitializationException {
		return AnalysisEngineFactory.createPrimitiveDescription(NestedAnnotationRemover.class,
				PARAM_TARGET_TYPE, targetTypeName);
	}

	public static final String PARAM_TARGET_TYPE = "targetType";

	@ConfigurationParameter(name = PARAM_TARGET_TYPE, mandatory = true)
	private String targetTypeName;
	// derived
	private Type targetType;
	// collection-scope state fields
	private int annotationsInspected;
	private int annotationsRemoved;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		targetType = ts.getType(targetTypeName);
		annotationTypeExist(targetTypeName, targetType);
	}

	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		// collect annotations to remove
		Set<AnnotationFS> annotations2Remove = Sets.newHashSet();
		AnnotationIndex<AnnotationFS> targetAnnoIdx = cas.getAnnotationIndex(targetType);
		annotationsInspected += targetAnnoIdx.size();
		OverlapIndex<AnnotationFS> overlapIdx = AnnotationUtils.createOverlapIndex(
				targetAnnoIdx.iterator());
		for (AnnotationFS anno : targetAnnoIdx) {
			// longer annotations come first in the default index
			if (annotations2Remove.contains(anno)) {
				continue;
			}
			Set<AnnotationFS> overlapping = overlapIdx.getOverlapping(
					anno.getBegin(), anno.getEnd());
			// remote itself from overlapping
			overlapping.remove(anno);
			if (!overlapping.isEmpty()) {
				annotations2Remove.addAll(overlapping);
				if (getLogger().isDebugEnabled()) {
					getLogger()
							.debug(
									String.format(
											"The following annotations overlap with %s and will be deleted:\n%s",
											toPrettyString(anno),
											Collections2.transform(overlapping,
													coveredTextFunction())));
				}
			}
		}
		for (AnnotationFS anno : annotations2Remove) {
			cas.removeFsFromIndexes(anno);
		}
		annotationsRemoved += annotations2Remove.size();
		if (getLogger().isTraceEnabled()) {
			getLogger().trace(String.format(
					"%s nested %ss were removed from %s",
					annotationsRemoved, targetType.getShortName(), getDocumentUri(cas)));
		}
	}

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		super.collectionProcessComplete();
		getLogger().info(String.format(
				"Report for %s:\n"
						+ "inspected: %s\n"
						+ "removed: %s",
				targetType.getName(), annotationsInspected, annotationsRemoved));
		annotationsInspected = 0;
		annotationsRemoved = 0;
	}

}
