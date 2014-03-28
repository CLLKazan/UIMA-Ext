package ru.kfu.itis.issst.corpus.statistics.cpe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Test;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.ExternalResourceFactory;

import ru.kfu.itis.cll.uima.util.DocumentUtils;
import ru.kfu.itis.issst.corpus.statistics.dao.XmiFileTreeCorpusDAO;

import com.google.common.collect.Sets;

public class CorpusDAOCollectionReaderTest {

	String corpusPathString = "src/test/resources/corpus_example";
	ExternalResourceDescription daoDesc;
	CollectionReader reader;

	@Before
	public void setUp() throws Exception {
		daoDesc = ExternalResourceFactory.createExternalResourceDescription(
				XmiFileTreeCorpusDAOResource.class, corpusPathString);
		reader = CollectionReaderFactory.createCollectionReader(
				CorpusDAOCollectionReader.class,
				XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString),
				CorpusDAOCollectionReader.CORPUS_DAO_KEY, daoDesc);
	}

	@Test
	public void testGetNext() throws CollectionException, IOException,
			ResourceInitializationException, URISyntaxException {
		Set<String> sourceUris = new HashSet<String>();
		while (reader.hasNext()) {
			CAS aCAS = CasCreationUtils.createCas(
					XmiFileTreeCorpusDAO.getTypeSystem(corpusPathString), null,
					null, null);
			reader.getNext(aCAS);
			assertTrue(aCAS.getDocumentText().contains("д"));
			String sourceUri = DocumentUtils.getDocumentUri(aCAS);
			sourceUris.add(sourceUri.substring(sourceUri.length() - 11));
		}
		assertEquals(Sets.newHashSet("1/62007.txt", "1/65801.txt",
				"5/62007.txt", "5/75788.txt"), sourceUris);
	}

}
