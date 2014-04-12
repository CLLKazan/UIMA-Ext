package ru.kfu.itis.issst.corpus.statistics.cpe;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.util.CasUtil;

import ru.kfu.itis.issst.corpus.statistics.dao.XmiFileTreeCorpusDAO;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class UnitClassifierTest {
	String corpusPathString = Thread.currentThread().getContextClassLoader()
			.getResource("corpus_example").getPath();
	Set<String> unitTypes = Sets
			.newHashSet("ru.kfu.cll.uima.tokenizer.fstype.W");
	Set<String> classTypes = Sets.newHashSet("ru.kfu.itis.issst.evex.Person",
			"ru.kfu.itis.issst.evex.Organization",
			"ru.kfu.itis.issst.evex.Artifact", "ru.kfu.itis.issst.evex.Weapon");
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
		SetMultimap<String, String> unitsByClass = HashMultimap.create();
		for (JCas jCas : new JCasIterable(reader, tokenizerSentenceSplitter,
				unitAnnotator, unitClassifier)) {
			CAS aCas = jCas.getCas();
			Type unitType = aCas.getTypeSystem().getType(
					UnitAnnotator.UNIT_TYPE_NAME);
			Feature classFeature = unitType
					.getFeatureByBaseName(UnitClassifier.CLASS_FEAT_NAME);
			for (AnnotationFS unitAnnotation : CasUtil.select(aCas, unitType)) {
				if (unitAnnotation.getStringValue(classFeature) != null) {
					unitsByClass.put(
							unitAnnotation.getStringValue(classFeature),
							unitAnnotation.getCoveredText());
				}
			}
		}
		assertEquals(
				Sets.newHashSet("Вагнер", "двое", "боевиков", "пособница"),
				unitsByClass.get("ru.kfu.itis.issst.evex.Person"));
		assertEquals(Sets.newHashSet("ЦСКА"),
				unitsByClass.get("ru.kfu.itis.issst.evex.Organization"));
		assertEquals(Sets.newHashSet(),
				unitsByClass.get("ru.kfu.itis.issst.evex.Artifact"));
		assertEquals(Sets.newHashSet("штурма", "квартиры"),
				unitsByClass.get("ru.kfu.itis.issst.evex.Weapon"));
	}

}
