package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.CasUtil;

import ru.kfu.itis.issst.corpus.statistics.dao.units.UnitsDAO;

public class UnitsDAOWriter extends CasAnnotator_ImplBase {

	final static String UNITS_DAO_KEY = "UnitsDAO";
	@ExternalResource(key = UNITS_DAO_KEY)
	private UnitsDAO unitsDAO;

	private Type unitType;
	private Feature classFeature;

	private Type sourceDocumentInformationType;
	private Feature uriFeature;
	private Feature annotatorIdFeature;

	@Override
	public void typeSystemInit(TypeSystem aTypeSystem)
			throws AnalysisEngineProcessException {
		unitType = aTypeSystem.getType(UnitAnnotator.UNIT_TYPE_NAME);
		classFeature = unitType
				.getFeatureByBaseName(UnitClassifier.CLASS_FEAT_NAME);

		sourceDocumentInformationType = aTypeSystem
				.getType(CorpusDAOCollectionReader.SOURCE_DOCUMENT_INFORMATION_TYPE_NAME);
		uriFeature = sourceDocumentInformationType
				.getFeatureByBaseName(CorpusDAOCollectionReader.URI_FEAT_NAME);
		annotatorIdFeature = sourceDocumentInformationType
				.getFeatureByBaseName(CorpusDAOCollectionReader.ANNOTATOR_ID_FEAT_NAME);

	}

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		for (AnnotationFS unit : CasUtil.select(aCAS, unitType)) {
			FeatureStructure sourceDocumentInformation = CasUtil.selectSingle(
					aCAS, sourceDocumentInformationType);
			try {
				unitsDAO.addUnitItem(
						new URI(sourceDocumentInformation
								.getStringValue(uriFeature)), unit.getBegin(),
						unit.getEnd(), sourceDocumentInformation
								.getStringValue(annotatorIdFeature), unit
								.getStringValue(classFeature));
			} catch (CASRuntimeException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
}
