package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.IOException;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.pipeline.SimplePipeline;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.corpus.statistics.dao.XmiFileTreeCorpusDAO;

import com.google.common.collect.Sets;

public class UnitAnnotatorTest {
	String corpusPathString = Thread.currentThread().getContextClassLoader().getResource("corpus_example").getPath();
	Set<String> unitTypes = Sets.newHashSet("ru.kfu.cll.uima.tokenizer.fstype.W");
	ExternalResourceDescription daoDesc;
	CollectionReaderDescription reader;
	AnalysisEngineDescription tokenizerSentenceSplitter;
	AnalysisEngineDescription unitAnnotator;

	@Before
	public void setUp() throws Exception {
		daoDesc = ExternalResourceFactory.createExternalResourceDescription(
				XmiFileTreeCorpusDAOResource.class, corpusPathString);
		reader = CollectionReaderFactory.createDescription(
				CorpusDAOCollectionReader.class,
				XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString),
				CorpusDAOCollectionReader.CORPUS_DAO_KEY, daoDesc);
		tokenizerSentenceSplitter = Unitizer.createTokenizerSentenceSplitterAED();
		unitAnnotator = AnalysisEngineFactory.createPrimitiveDescription(
				UnitAnnotator.class, UnitAnnotator.PARAM_UNIT_TYPE_NAMES,
				unitTypes);
	}

	@Test
	public void test() throws UIMAException, SAXException,
			CpeDescriptorException, IOException {
		SimplePipeline.runPipeline(reader, tokenizerSentenceSplitter, unitAnnotator);
	}

}
