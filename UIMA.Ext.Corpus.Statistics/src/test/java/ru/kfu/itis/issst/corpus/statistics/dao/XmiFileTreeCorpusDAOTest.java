package ru.kfu.itis.issst.corpus.statistics.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
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
}
