package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.corpus.statistics.dao.CorpusDAO;
import ru.kfu.itis.issst.corpus.statistics.dao.XmiFileTreeCorpusDAO;

public class XmiFileTreeCorpusDAOResource implements CorpusDAO,
		SharedResourceObject {

	private CorpusDAO corpusDAO;

	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		try {
			System.out.println(aData.getUri());
			corpusDAO = new XmiFileTreeCorpusDAO(aData.getUri().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new ResourceInitializationException();
		}
	}

	public Set<URI> getDocuments() throws URISyntaxException {
		return corpusDAO.getDocuments();
	}

	public Set<String> getAnnotatorIds(URI docURI) throws IOException {
		return corpusDAO.getAnnotatorIds(docURI);
	}

	public void getDocumentCas(URI docURI, String annotatorId, CAS aCAS)
			throws IOException, SAXException {
		corpusDAO.getDocumentCas(docURI, annotatorId, aCAS);
	}
}
