package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.CasUtil;

import com.google.common.collect.Sets;

public class UnitClassifier extends CasAnnotator_ImplBase {

	public static final String UNIT_TYPE_NAME = "ru.kfu.itis.issst.corpus.statistics.type.Unit";
	public static final String CLASS_FEAT_NAME = "class";

	public static final String PARAM_CLASS_TYPE_NAMES = "classTypeNames";
	@ConfigurationParameter(name = PARAM_CLASS_TYPE_NAMES, mandatory = true, description = "Set of class type names for classifying units")
	private Set<String> classTypeNames;

	private Type unitType;
	private Feature classFeature;
	private Set<Type> classTypes = Sets.newHashSet();

	@Override
	public void typeSystemInit(TypeSystem aTypeSystem)
			throws AnalysisEngineProcessException {
		unitType = aTypeSystem.getType(UNIT_TYPE_NAME);
		classFeature = unitType.getFeatureByBaseName(CLASS_FEAT_NAME);
		for (String classTypeName : classTypeNames) {
			classTypes.add(aTypeSystem.getType(classTypeName));
		}
	}

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		for (AnnotationFS unit : CasUtil.select(aCAS, unitType)) {
			for (Type classType : classTypes) {
				for (AnnotationFS classAnnotation : CasUtil.select(aCAS,
						classType)) {
					if (isIntersect(unit, classAnnotation)) {
						unit.setStringValue(classFeature, classType.getName());
					}
				}
			}
		}
	}

	private static boolean isIntersect(AnnotationFS first, AnnotationFS second) {
		// See http://stackoverflow.com/a/3269471 if you don't get it
		return first.getBegin() < second.getEnd()
				&& second.getBegin() < first.getEnd();
	}
}
