package ru.kfu.itis.issst.corpus.statistics.dao;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.cas.CAS;

public class XmiFileTreeCorpusDAO implements CorpusDAO {

	private File corpusFile;
	private File[] annotatorDirs;

	public XmiFileTreeCorpusDAO(String corpusPathString) {
		corpusFile = new File(corpusPathString);
		annotatorDirs = corpusFile.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
	}

	@Override
	public List<URI> getDocs() {
		List<URI> docsList = new ArrayList<URI>();
		for (File annotatorDir : annotatorDirs) {
			for (File xmiFile : annotatorDir.listFiles()) {
				docsList.add(xmiFile.toURI());
			}
		}
		return docsList;
	}

	@Override
	public String getAnnotatorId(URI docURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CAS getDocumentCas(URI docURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
