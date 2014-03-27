package ru.kfu.itis.issst.corpus.statistics.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.junit.Test;

import com.google.common.collect.Sets;

public class XmiFileTreeCorpusDAOTest {

	private String corpusPathString = "src/test/resources/corpus_example";
	CorpusDAO corpusDAO = new XmiFileTreeCorpusDAO(corpusPathString);

	@Test
	public void testGetDocs() {
		for (URI docURI : corpusDAO.getDocs()) {
			assertTrue(new File(docURI).exists());
		}
		assertEquals(4, corpusDAO.getDocs().size());
	}

	@Test
	public void testGetAnnotatorId() {
		Set<String> annotatorIds = new HashSet<String>();
		for (URI docURI : corpusDAO.getDocs()) {
			annotatorIds.add(corpusDAO.getAnnotatorId(docURI));
		}
		assertEquals(Sets.newHashSet("1", "5"), annotatorIds);
	}

	@Test
	public void testGetTypeSystem() throws ResourceInitializationException,
			InvalidXMLException {
		TypeSystemDescription typeSystem = corpusDAO.getTypeSystem();
		typeSystem.resolveImports();

		Set<String> typeNames = new HashSet<String>();
		for (TypeDescription type : typeSystem.getTypes()) {
			System.out.println(type.getName());
			typeNames.add(type.getName());
		}

		assertEquals(Sets.newHashSet(
				"ru.kfu.itis.cll.uima.commons.DocumentMetadata",
				"ru.kfu.itis.issst.evex.Person",
				"ru.kfu.itis.issst.evex.Organization",
				"ru.kfu.itis.issst.evex.Weapon",
				"ru.kfu.itis.issst.evex.Event", "ru.kfu.itis.issst.evex.Die"),
				typeNames);
	}
}
