package ru.kfu.itis.issst.corpus.statistics.dao;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.InvalidXMLException;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.util.CasUtil;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.corpus.statistics.dao.corpus.CorpusDAO;
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.XmiFileTreeCorpusDAO;

import com.google.common.collect.Sets;

public class XmiFileTreeCorpusDAOTest {

	String corpusPathString = "src/test/resources/corpus_example";
	CorpusDAO corpusDAO;

	@Before
	public void setUp() throws URISyntaxException {
		corpusDAO = new XmiFileTreeCorpusDAO(corpusPathString);
	}

	@Test
	public void testGetDocuments() throws URISyntaxException {
		assertEquals(Sets.newHashSet(new URI("62007.txt"),
				new URI("65801.txt"), new URI("75788.txt")),
				corpusDAO.getDocuments());
	}

	@Test
	public void testGetAnnotatorIds() throws URISyntaxException, IOException {
		assertEquals(Sets.newHashSet("1", "5"),
				corpusDAO.getAnnotatorIds(new URI("62007.txt")));
		assertEquals(Sets.newHashSet("1"),
				corpusDAO.getAnnotatorIds(new URI("65801.txt")));
		assertEquals(Sets.newHashSet("5"),
				corpusDAO.getAnnotatorIds(new URI("75788.txt")));
	}

	@Test(expected = FileNotFoundException.class)
	public void testGetAnnotatorIdsForNonexistingDocument()
			throws URISyntaxException, IOException {
		corpusDAO.getAnnotatorIds(new URI("49580.txt"));
	}

	@Test
	public void testGetTypeSystem() throws ResourceInitializationException,
			InvalidXMLException, SAXException, IOException, ParserConfigurationException {
		TypeSystemDescription typeSystem = XmiFileTreeCorpusDAO
				.getTypeSystem(corpusPathString);
		typeSystem.resolveImports();

		Set<String> typeNames = new HashSet<String>();
		for (TypeDescription type : typeSystem.getTypes()) {
			typeNames.add(type.getName());
		}

		assertEquals(Sets.newHashSet(
				"ru.kfu.itis.cll.uima.commons.DocumentMetadata",
				"ru.kfu.itis.issst.evex.Person",
				"ru.kfu.itis.issst.evex.Organization",
				"ru.kfu.itis.issst.evex.Artifact",
				"ru.kfu.itis.issst.evex.Weapon", "ru.kfu.itis.issst.evex.Job",
				"ru.kfu.itis.issst.evex.Time", "ru.kfu.itis.issst.evex.Event",
				"ru.kfu.itis.issst.evex.Die",
				"ru.kfu.itis.issst.evex.StartPosition"), typeNames);
	}

	@Test
	public void testGetDocumentCas() throws ResourceInitializationException,
			IOException, SAXException, URISyntaxException, ParserConfigurationException {
		CAS aCAS = CasCreationUtils.createCas(
				XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString), null,
				null, null);
		corpusDAO.getDocumentCas(new URI("62007.txt"), "1", aCAS);
		assertThat(aCAS.getDocumentText(), containsString("РИА Новости"));
		assertEquals(6, CasUtil.selectAll(aCAS).size());
		assertEquals(
				1,
				CasUtil.select(
						aCAS,
						CasUtil.getAnnotationType(aCAS,
								"ru.kfu.itis.issst.evex.Weapon")).size());

		aCAS = CasCreationUtils.createCas(
				XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString), null,
				null, null);
		corpusDAO.getDocumentCas(new URI("62007.txt"), "5", aCAS);
		assertThat(aCAS.getDocumentText(), containsString("РИА Новости"));
		assertThat(CasUtil.selectAll(aCAS).size(), equalTo(5));
		assertEquals(
				0,
				CasUtil.select(
						aCAS,
						CasUtil.getAnnotationType(aCAS,
								"ru.kfu.itis.issst.evex.Weapon")).size());
	}

	@Test(expected = FileNotFoundException.class)
	public void testGetDocumentCasForNonexistingDocument()
			throws ResourceInitializationException, IOException, SAXException,
			URISyntaxException, ParserConfigurationException {
		CAS aCAS = CasCreationUtils.createCas(
				XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString), null,
				null, null);
		corpusDAO.getDocumentCas(new URI("49053.txt"), "1", aCAS);
	}

	@Test(expected = FileNotFoundException.class)
	public void testGetDocumentCasForWrongAnnotator()
			throws ResourceInitializationException, IOException, SAXException,
			URISyntaxException, ParserConfigurationException {
		CAS aCAS = CasCreationUtils.createCas(
				XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString), null,
				null, null);
		corpusDAO.getDocumentCas(new URI("75788.txt"), "1", aCAS);
	}

}
