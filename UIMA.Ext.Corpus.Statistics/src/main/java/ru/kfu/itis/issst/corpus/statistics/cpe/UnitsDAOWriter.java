package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.CasUtil;

import ru.kfu.itis.issst.corpus.statistics.dao.units.UnitsDAO;

public class UnitsDAOWriter extends CasAnnotator_ImplBase {

	final static String UNITS_TSV_PATH = "UnitsTSVPath";
	@ConfigurationParameter(name = UNITS_TSV_PATH, defaultValue = "units.tsv", description = "Path to file to write units")
	private File unitsTSV;

	final static String UNITS_DAO_IMPLEMENTATION_CLASS_NAME = "UnitsDAOImplementationClassName";
	@ConfigurationParameter(name = UNITS_DAO_IMPLEMENTATION_CLASS_NAME, defaultValue = "ru.kfu.itis.issst.corpus.statistics.dao.units.InMemoryUnitsDAO", description = "Class name of UnitsDAO implementation")
	private String unitsDAOImplementationClassName;
	private UnitsDAO unitsDAO;

	private Type unitType;
	private Feature classFeature;

	private Type sourceDocumentInformationType;
	private Feature uriFeature;
	private Feature annotatorIdFeature;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		try {
			unitsDAO = (UnitsDAO) Class.forName(unitsDAOImplementationClassName).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

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
