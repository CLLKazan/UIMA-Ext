package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.IOException;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.pipeline.SimplePipeline;

import ru.kfu.itis.issst.corpus.statistics.dao.XmiFileTreeCorpusDAO;

import com.google.common.collect.Sets;

public class UnitClassifierTest {
	String corpusPathString = Thread.currentThread().getContextClassLoader()
			.getResource("corpus_example").getPath();
	Set<String> unitTypes = Sets
			.newHashSet("ru.kfu.cll.uima.tokenizer.fstype.W");
	Set<String> classTypes = Sets.newHashSet("ru.kfu.itis.issst.evex.Person");
	ExternalResourceDescription daoDesc;
	CollectionReader reader;
	AnalysisEngine tokenizerSentenceSplitter;
	AnalysisEngine unitAnnotator;
	AnalysisEngine unitClassifier;

	@Before
	public void setUp() throws Exception {
		daoDesc = ExternalResourceFactory.createExternalResourceDescription(
				XmiFileTreeCorpusDAOResource.class, corpusPathString);
		reader = CollectionReaderFactory.createCollectionReader(
				CorpusDAOCollectionReader.class,
				XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString),
				CorpusDAOCollectionReader.CORPUS_DAO_KEY, daoDesc);
		tokenizerSentenceSplitter = AnalysisEngineFactory
				.createAggregate(Unitizer.createTokenizerSentenceSplitterAED());
		unitAnnotator = AnalysisEngineFactory.createPrimitive(
				UnitAnnotator.class, UnitAnnotator.PARAM_UNIT_TYPE_NAMES,
				unitTypes);
		unitClassifier = AnalysisEngineFactory.createPrimitive(
				UnitClassifier.class, UnitClassifier.PARAM_CLASS_TYPE_NAMES,
				classTypes);
	}

	@Test
	public void test() throws UIMAException, IOException {
		SimplePipeline.runPipeline(reader, tokenizerSentenceSplitter,
				unitAnnotator, unitClassifier);
	}

}
