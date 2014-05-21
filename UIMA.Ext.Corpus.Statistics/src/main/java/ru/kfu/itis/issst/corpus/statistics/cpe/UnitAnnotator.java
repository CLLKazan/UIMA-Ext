package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.CasUtil;

import com.google.common.collect.Sets;

public class UnitAnnotator extends CasAnnotator_ImplBase {

	public static final String UNIT_TYPE_NAME = "ru.kfu.itis.issst.corpus.statistics.type.Unit";

	public static final String PARAM_UNIT_TYPE_NAMES = "unitTypeNames";
	@ConfigurationParameter(name = PARAM_UNIT_TYPE_NAMES, mandatory = true, description = "Set of unit type names, for which unit annotation will be created")
	private Set<String> unitTypeNames;

	private Type unitType;
	private Set<Type> unitTypes = Sets.newHashSet();

	@Override
	public void typeSystemInit(TypeSystem aTypeSystem)
			throws AnalysisEngineProcessException {
		unitType = aTypeSystem.getType(UNIT_TYPE_NAME);
		for (String unitTypeName : unitTypeNames) {
			unitTypes.add(aTypeSystem.getType(unitTypeName));
		}
	}

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		for (Type unitSourceType : unitTypes) {
			for (AnnotationFS unitSource : CasUtil.select(aCAS, unitSourceType)) {
				AnnotationFS unit = aCAS.createAnnotation(unitType,
						unitSource.getBegin(), unitSource.getEnd());
				aCAS.addFsToIndexes(unit);
			}
		}
	}
}
