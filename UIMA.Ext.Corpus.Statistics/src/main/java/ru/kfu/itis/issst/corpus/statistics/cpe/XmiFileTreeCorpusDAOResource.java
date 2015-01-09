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

import ru.kfu.itis.issst.corpus.statistics.dao.corpus.CorpusDAO;
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.XmiFileTreeCorpusDAO;

public class XmiFileTreeCorpusDAOResource implements CorpusDAO,
		SharedResourceObject {

	private CorpusDAO corpusDAO;

	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		try {
			corpusDAO = new XmiFileTreeCorpusDAO(aData.getUri().toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new ResourceInitializationException();
		}
	}

	@Override
	public Set<URI> getDocuments() throws URISyntaxException {
		return corpusDAO.getDocuments();
	}

	@Override
	public Set<String> getAnnotatorIds(URI docURI) throws IOException {
		return corpusDAO.getAnnotatorIds(docURI);
	}

	@Override
	public void getDocumentCas(URI docURI, String annotatorId, CAS aCAS)
			throws IOException, SAXException {
		corpusDAO.getDocumentCas(docURI, annotatorId, aCAS);
	}

	@Override
	public boolean hasDocument(URI docURI, String annotatorId) {
		return corpusDAO.hasDocument(docURI, annotatorId);
	}

	@Override
	public void persist(URI docUri, String annotatorId, CAS cas) throws IOException, SAXException {
		corpusDAO.persist(docUri, annotatorId, cas);
	}

}
