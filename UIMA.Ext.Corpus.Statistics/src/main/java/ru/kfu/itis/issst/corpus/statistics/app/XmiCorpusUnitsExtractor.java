package ru.kfu.itis.issst.corpus.statistics.app;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.CpeBuilder;
import org.uimafit.factory.ExternalResourceFactory;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.corpus.statistics.cpe.CorpusDAOCollectionReader;
import ru.kfu.itis.issst.corpus.statistics.cpe.UnitAnnotator;
import ru.kfu.itis.issst.corpus.statistics.cpe.UnitClassifier;
import ru.kfu.itis.issst.corpus.statistics.cpe.Unitizer;
import ru.kfu.itis.issst.corpus.statistics.cpe.UnitsDAOWriter;
import ru.kfu.itis.issst.corpus.statistics.cpe.XmiFileTreeCorpusDAOResource;
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.XmiFileTreeCorpusDAO;

import com.beust.jcommander.JCommander;

public class XmiCorpusUnitsExtractor {

	private ExternalResourceDescription daoDesc;
	private TypeSystemDescription tsd;
	private CollectionReaderDescription reader;
	private AnalysisEngineDescription tokenizerSentenceSplitter;
	private AnalysisEngineDescription unitAnnotator;
	private AnalysisEngineDescription unitClassifier;
	private AnalysisEngineDescription unitsDAOWriter;
	private AnalysisEngineDescription aggregate;
	private CollectionProcessingEngine cpe;

	public static void main(String[] args)
			throws IOException, SAXException,
			CpeDescriptorException, ParserConfigurationException, UIMAException {
		XmiCorpusUnitsExtractor extractor = new XmiCorpusUnitsExtractor(args);
		extractor.process();
	}

	XmiCorpusUnitsExtractor(String[] args)
			throws IOException, SAXException,
			CpeDescriptorException, ParserConfigurationException, UIMAException {
		XmiCorpusUnitsExtractorParams extractorParams = new XmiCorpusUnitsExtractorParams();
		new JCommander(extractorParams, args);

		CpeBuilder cpeBuilder = new CpeBuilder();

		daoDesc = ExternalResourceFactory.createExternalResourceDescription(
				XmiFileTreeCorpusDAOResource.class, extractorParams.corpus);
		tsd = XmiFileTreeCorpusDAO.getTypeSystem(extractorParams.corpus);
		reader = CollectionReaderFactory.createDescription(
				CorpusDAOCollectionReader.class, tsd,
				CorpusDAOCollectionReader.CORPUS_DAO_KEY, daoDesc);
		cpeBuilder.setReader(reader);

		tokenizerSentenceSplitter = AnalysisEngineFactory
				.createAggregateDescription(Unitizer
						.createTokenizerSentenceSplitterAED());

		unitAnnotator = AnalysisEngineFactory.createPrimitiveDescription(
				UnitAnnotator.class, UnitAnnotator.PARAM_UNIT_TYPE_NAMES,
				extractorParams.units);
		unitClassifier = AnalysisEngineFactory.createPrimitiveDescription(
				UnitClassifier.class, UnitClassifier.PARAM_CLASS_TYPE_NAMES,
				extractorParams.classes);

		unitsDAOWriter = AnalysisEngineFactory.createPrimitiveDescription(
				UnitsDAOWriter.class, UnitsDAOWriter.UNITS_TSV_PATH,
				extractorParams.output);
		aggregate = AnalysisEngineFactory.createAggregateDescription(
				tokenizerSentenceSplitter, unitAnnotator, unitClassifier,
				unitsDAOWriter);
		cpeBuilder.setAnalysisEngine(aggregate);

		cpe = cpeBuilder.createCpe(null);
	}

	public void process() throws ResourceInitializationException {
		cpe.process();
	}
}
