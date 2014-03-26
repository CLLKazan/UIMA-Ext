package ru.kfu.itis.issst.corpus.statistics.dao;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class XmiFileTreeCorpusDAOTest {
	
	private String corpusPathString = "src/test/resources/corpus_example";
	CorpusDAO corpusDAO = new XmiFileTreeCorpusDAO(corpusPathString);

	@Test
	public void testGetDocs() {
		for (URI docURI : corpusDAO.getDocs()) {
			assertTrue(new File(docURI).exists());
		}
		assertEquals(corpusDAO.getDocs().size(), 4);
	}

}
