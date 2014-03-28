package ru.kfu.itis.issst.corpus.statistics.dao;

import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XmlCasDeserializer;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class XmiFileTreeCorpusDAO implements CorpusDAO {

	private Map<UriAnnotatorPair, File> fileByURIandAnnotatorId = new HashMap<UriAnnotatorPair, File>();
	private SetMultimap<URI, String> annotatorsByDocument = HashMultimap
			.create();

	public XmiFileTreeCorpusDAO(String corpusPathString)
			throws URISyntaxException {
		File corpusDirFile = new File(corpusPathString);
		findFiles(corpusDirFile);
	}

	private void findFiles(File corpusDirFile) throws URISyntaxException {
		for (File dir : listAnnotatorDirs(corpusDirFile)) {
			for (File xmiFile : dir.listFiles()) {
				URI uri = getDocumentURI(xmiFile);
				String annotatorId = getAnnotatorId(xmiFile);
				fileByURIandAnnotatorId.put(new UriAnnotatorPair(uri,
						annotatorId), xmiFile);
				annotatorsByDocument.put(uri, getAnnotatorId(xmiFile));
			}
		}
	}

	private File[] listAnnotatorDirs(File corpusDir) {
		return corpusDir.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
	}

	private String getAnnotatorId(File xmiFile) {
		return xmiFile.getParentFile().getName();
	}

	private URI getDocumentURI(File xmiFile) throws URISyntaxException {
		return new URI(FilenameUtils.removeExtension(xmiFile.getName()));
	}

	@Override
	public Set<URI> getDocuments() throws URISyntaxException {
		return annotatorsByDocument.keySet();
	}

	@Override
	public Set<String> getAnnotatorIds(URI docURI) throws FileNotFoundException {
		if (annotatorsByDocument.containsKey(docURI)) {
			return annotatorsByDocument.get(docURI);
		} else {
			throw new FileNotFoundException();
		}
	}

	@Override
	public void getDocumentCas(URI docURI, String annotatorId, CAS aCAS)
			throws SAXException, IOException {
		if (fileByURIandAnnotatorId.containsKey(new UriAnnotatorPair(docURI,
				annotatorId))) {
			File xmiFile = fileByURIandAnnotatorId.get(new UriAnnotatorPair(
					docURI, annotatorId));
			XmlCasDeserializer.deserialize(new FileInputStream(xmiFile), aCAS);
		} else {
			throw new FileNotFoundException();
		}
	}

	static public TypeSystemDescription getTypeSystem(String corpusPathString) {
		File typeSystemFile = new File(corpusPathString)
				.listFiles((FileFilter) new WildcardFileFilter("*.xml"))[0];
		return createTypeSystemDescriptionFromPath(typeSystemFile.toString());
	}
}
