package ru.kfu.itis.issst.corpus.statistics.cpe;

import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;
import org.uimafit.pipeline.SimplePipeline;

import ru.kfu.itis.issst.corpus.statistics.dao.corpus.XmiFileTreeCorpusDAO;

import com.google.common.collect.Sets;

public class UnitsDAOWriterTest {
	String corpusPathString = Thread.currentThread().getContextClassLoader()
			.getResource("corpus_example").getPath();

	Set<String> unitTypes = Sets
			.newHashSet("ru.kfu.cll.uima.tokenizer.fstype.W");
	Set<String> classTypes = Sets.newHashSet("ru.kfu.itis.issst.evex.Person",
			"ru.kfu.itis.issst.evex.Organization",
			"ru.kfu.itis.issst.evex.Weapon");
	ExternalResourceDescription daoDesc;
	TypeSystemDescription tsd;
	CollectionReader reader;
	AnalysisEngine tokenizerSentenceSplitter;
	AnalysisEngine unitAnnotator;
	AnalysisEngine unitClassifier;
	AnalysisEngine unitsDAOWriter;

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	private File unitsTSV;

	@Before
	public void setUp() throws Exception {
		daoDesc = ExternalResourceFactory.createExternalResourceDescription(
				XmiFileTreeCorpusDAOResource.class, corpusPathString);
		tsd = CasCreationUtils
				.mergeTypeSystems(Sets.newHashSet(
						XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString),
						TypeSystemDescriptionFactory
								.createTypeSystemDescription(),
						createTypeSystemDescription("ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem"),
						createTypeSystemDescription("ru.kfu.cll.uima.segmentation.segmentation-TypeSystem")));
		reader = CollectionReaderFactory.createCollectionReader(
				CorpusDAOCollectionReader.class, tsd,
				CorpusDAOCollectionReader.CORPUS_DAO_KEY, daoDesc);
		CAS aCAS = CasCreationUtils.createCas(tsd, null, null, null);
		reader.typeSystemInit(aCAS.getTypeSystem());
		tokenizerSentenceSplitter = AnalysisEngineFactory
				.createAggregate(Unitizer.createTokenizerSentenceSplitterAED());
		unitAnnotator = AnalysisEngineFactory.createPrimitive(
				UnitAnnotator.class, UnitAnnotator.PARAM_UNIT_TYPE_NAMES,
				unitTypes);
		unitClassifier = AnalysisEngineFactory.createPrimitive(
				UnitClassifier.class, UnitClassifier.PARAM_CLASS_TYPE_NAMES,
				classTypes);

		unitsTSV = tempFolder.newFile();
		unitsDAOWriter = AnalysisEngineFactory.createPrimitive(
				UnitsDAOWriter.class, UnitsDAOWriter.UNITS_TSV_PATH,
				unitsTSV.getPath());
	}

	@Test
	public void test() throws CASRuntimeException, UIMAException, IOException {
		SimplePipeline.runPipeline(reader, tokenizerSentenceSplitter,
				unitAnnotator, unitClassifier, unitsDAOWriter);
	}

}
