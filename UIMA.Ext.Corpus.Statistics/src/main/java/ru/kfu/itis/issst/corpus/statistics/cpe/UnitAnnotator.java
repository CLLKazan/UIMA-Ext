package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.util.CasUtil;

public class UnitAnnotator extends CasAnnotator_ImplBase {

	public static final String UNIT_TYPE_NAME = "ru.kfu.itis.issst.corpus.statistics.type.Unit";

	public static final String PARAM_UNIT_TYPE_NAMES = "unitTypeNames";
	@ConfigurationParameter(name = PARAM_UNIT_TYPE_NAMES, mandatory = true, description = "Set of unit ype names, for which unit annotation will be created")
	private Set<String> unitTypeNames;

	@Override
	public void process(CAS aCas) throws AnalysisEngineProcessException {
		for (String unitTypeName : unitTypeNames) {
			Type unitSourceType = CasUtil.getType(aCas, unitTypeName);
			Type unitType = aCas.getTypeSystem().getType(UNIT_TYPE_NAME);
			for (AnnotationFS unitSource : CasUtil.select(aCas, unitSourceType)) {
				AnnotationFS unit = aCas.createAnnotation(unitType,
						unitSource.getBegin(), unitSource.getEnd());
				aCas.addFsToIndexes(unit);
			}
		}
	}
}
