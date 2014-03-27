package ru.kfu.itis.issst.corpus.statistics.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

public interface CorpusDAO {

	List<URI> getDocs();

	String getAnnotatorId(URI docURI);

	void getDocumentCas(URI docURI, CAS aCAS) throws FileNotFoundException,
			SAXException, IOException;

	TypeSystemDescription getTypeSystem();

}
