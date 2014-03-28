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

	private File corpusFile;
	private File[] annotatorDirs;
	private Map<UriAnnotatorPair, File> fileByURIandAnnotatorId = new HashMap<UriAnnotatorPair, File>();
	private SetMultimap<URI, String> annotatorsByDocument = HashMultimap
			.create();

	public XmiFileTreeCorpusDAO(String corpusPathString)
			throws URISyntaxException {
		corpusFile = new File(corpusPathString);
		annotatorDirs = corpusFile
				.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		findFiles();
	}

	private void findFiles() throws URISyntaxException {
		for (File dir : annotatorDirs) {
			for (File xmiFile : dir.listFiles()) {
				URI uri = getDocumentURI(xmiFile);
				String annotatorId = getAnnotatorId(xmiFile);
				fileByURIandAnnotatorId.put(new UriAnnotatorPair(uri,
						annotatorId), xmiFile);
				annotatorsByDocument.put(uri, getAnnotatorId(xmiFile));
			}
		}
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
		if (fileByURIandAnnotatorId.containsKey(new UriAnnotatorPair(docURI, annotatorId))) {
			XmlCasDeserializer.deserialize(
					new FileInputStream(fileByURIandAnnotatorId
							.get(new UriAnnotatorPair(docURI, annotatorId))), aCAS);
		} else {
			throw new FileNotFoundException();
		}
	}

	@Override
	public TypeSystemDescription getTypeSystem() {
		File typeSystemFile = corpusFile
				.listFiles((FileFilter) new WildcardFileFilter("*.xml"))[0];
		return createTypeSystemDescriptionFromPath(typeSystemFile.toString());
	}

	private static class UriAnnotatorPair {
		public final URI uri;
		public final String annotatorId;

		public UriAnnotatorPair(URI uri, String annotatorId) {
			this.uri = uri;
			this.annotatorId = annotatorId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((annotatorId == null) ? 0 : annotatorId.hashCode());
			result = prime * result + ((uri == null) ? 0 : uri.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UriAnnotatorPair other = (UriAnnotatorPair) obj;
			if (annotatorId == null) {
				if (other.annotatorId != null)
					return false;
			} else if (!annotatorId.equals(other.annotatorId))
				return false;
			if (uri == null) {
				if (other.uri != null)
					return false;
			} else if (!uri.equals(other.uri))
				return false;
			return true;
		}
	}
}
