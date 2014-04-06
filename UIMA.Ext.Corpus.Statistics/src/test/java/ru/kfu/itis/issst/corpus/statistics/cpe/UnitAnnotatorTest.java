package ru.kfu.itis.issst.corpus.statistics.cpe;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.CasUtil;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.corpus.statistics.dao.XmiFileTreeCorpusDAO;

import com.google.common.collect.Sets;

public class UnitAnnotatorTest {
	String corpusPathString = Thread.currentThread().getContextClassLoader()
			.getResource("corpus_example").getPath();
	Set<String> unitTypes = Sets
			.newHashSet("ru.kfu.cll.uima.tokenizer.fstype.W");
	ExternalResourceDescription daoDesc;
	CollectionReader reader;
	AnalysisEngine tokenizerSentenceSplitter;
	AnalysisEngine unitAnnotator;

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
	}

	@Test
	public void test() throws UIMAException, SAXException,
			CpeDescriptorException, IOException {
		for (JCas jCas : new JCasIterable(reader, tokenizerSentenceSplitter,
				unitAnnotator)) {
			CAS aCas = jCas.getCas();
			Type unitSourceType = CasUtil.getType(aCas, unitTypes.iterator()
					.next());
			Type unitType = aCas.getTypeSystem().getType(
					UnitAnnotator.UNIT_TYPE_NAME);

			int sourceNumber = CasUtil.select(aCas, unitSourceType).size();
			int unitNumber = CasUtil.select(aCas, unitType).size();
			assertTrue(sourceNumber > 0);
			assertThat(unitNumber, equalTo(sourceNumber));
		}
	}
}
