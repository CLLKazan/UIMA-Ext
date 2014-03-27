package ru.kfu.itis.issst.corpus.statistics.dao;

import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XmlCasDeserializer;
import org.xml.sax.SAXException;

public class XmiFileTreeCorpusDAO implements CorpusDAO {

	private File corpusFile;
	private File[] annotatorDirs;

	public XmiFileTreeCorpusDAO(String corpusPathString) {
		corpusFile = new File(corpusPathString);
		annotatorDirs = corpusFile
				.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
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
		File f = new File(docURI);
		return f.getParentFile().getName();
	}

	@Override
	public void getDocumentCas(URI docURI, CAS aCAS) throws SAXException,
			IOException {
		XmlCasDeserializer.deserialize(new FileInputStream(new File(docURI)),
				aCAS);
	}

	@Override
	public TypeSystemDescription getTypeSystem() {
		File typeSystemFile = corpusFile
				.listFiles((FileFilter) new WildcardFileFilter("*.xml"))[0];
		return createTypeSystemDescriptionFromPath(typeSystemFile.toString());
	}

}
