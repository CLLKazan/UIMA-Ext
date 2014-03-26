package ru.kfu.itis.issst.corpus.statistics.dao;

import java.net.URI;
import java.util.List;

import org.apache.uima.cas.CAS;

public interface CorpusDAO {
	
	List<URI> getDocs();
	
	String getAnnotatorId(URI docURI);
	
	CAS getDocumentCas(URI docURI);

}
