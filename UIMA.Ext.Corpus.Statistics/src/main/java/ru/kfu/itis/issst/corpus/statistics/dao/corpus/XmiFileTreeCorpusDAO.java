package ru.kfu.itis.issst.corpus.statistics.dao.corpus;

import static org.apache.commons.io.FileUtils.forceMkdir;
import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLSerializer;
import org.apache.uima.util.XmlCasDeserializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class XmiFileTreeCorpusDAO implements CorpusDAO {

	public static final String XMI_FILE_EXTENSION = "xmi";
	// config fields
	private final File corpusBaseDir;
	// state fields
	// TODO encapsulate in a single object to avoid inconsistent changes
	private Map<UriAnnotatorPair, File> fileByURIandAnnotatorId =
			new HashMap<UriAnnotatorPair, File>();
	private SetMultimap<URI, String> annotatorsByDocument = HashMultimap.create();

	public XmiFileTreeCorpusDAO(String corpusPathString)
			throws URISyntaxException {
		corpusBaseDir = new File(corpusPathString);
		if (!corpusBaseDir.isDirectory()) {
			throw new IllegalStateException(String.format(
					"Corpus base dir %s does not exist!", corpusBaseDir));
		}
		findFiles(corpusBaseDir);
	}

	private void findFiles(File corpusDirFile) throws URISyntaxException {
		for (File dir : listAnnotatorDirs(corpusDirFile)) {
			// TODO check for file extension
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
		return dirNameToAnnotatorId(xmiFile.getParentFile());
	}

	private String dirNameToAnnotatorId(File dir) {
		return dir.getName();
	}

	private File annotatorIdToDir(String annotatorId) {
		return new File(corpusBaseDir, annotatorId);
	}

	private URI getDocumentURI(File xmiFile) throws URISyntaxException {
		return new URI(FilenameUtils.removeExtension(xmiFile.getName()));
	}

	private String getDocumentFilename(URI docUri) {
		// sanity check
		if (StringUtils.isBlank(docUri.getPath())) {
			throw new IllegalStateException(String.format(
					"Unexpected doc URI: %s", docUri));
		}
		return String.format("%s.%s", docUri.getPath(), XMI_FILE_EXTENSION);
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
			FileInputStream xmiFileIn = new FileInputStream(
					fileByURIandAnnotatorId.get(new UriAnnotatorPair(
							docURI, annotatorId)));
			XmlCasDeserializer.deserialize(xmiFileIn, aCAS);
			closeQuietly(xmiFileIn);
		} else {
			throw new FileNotFoundException(String.format(
					"There is no document '%s' annotated by '%s'",
					docURI, annotatorId));
		}
	}

	@Override
	public boolean hasDocument(URI docURI, String annotatorId) {
		return fileByURIandAnnotatorId.containsKey(
				new UriAnnotatorPair(docURI, annotatorId));
	}

	@Override
	public void persist(URI docUri, String annotatorId, CAS cas) throws IOException, SAXException {
		File annotatorDir = annotatorIdToDir(annotatorId);
		forceMkdir(annotatorDir);
		File docFile = new File(annotatorDir, getDocumentFilename(docUri));
		try {
			serializeCAS(cas, docFile);
		} catch (IOException ex) {
			// clean
			FileUtils.forceDelete(docFile);
			throw ex;
		} catch (SAXException ex) {
			// clean
			FileUtils.forceDelete(docFile);
			throw ex;
		}
		// update in-memory state
		fileByURIandAnnotatorId.put(new UriAnnotatorPair(docUri, annotatorId), docFile);
		annotatorsByDocument.put(docUri, annotatorId);
	}

	static private void serializeCAS(CAS cas, File outFile) throws IOException, SAXException {
		OutputStream out = null;
		try {
			out = FileUtils.openOutputStream(outFile);
			XmiCasSerializer xcs = new XmiCasSerializer(cas.getTypeSystem());
			XMLSerializer ser = new XMLSerializer(out, true);
			xcs.serialize(cas, ser.getContentHandler());
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	static public TypeSystemDescription getTypeSystem(String corpusPathString)
			throws SAXException, IOException, ParserConfigurationException {
		for (File f : new File(corpusPathString)
				.listFiles((FileFilter) new WildcardFileFilter("*.xml"))) {
			if (getXMLRootElement(f).equals("typeSystemDescription")) {
				return createTypeSystemDescriptionFromPath(f.toString());
			}
		}
		throw new FileNotFoundException();
	}

	static private String getXMLRootElement(File xmlFile) throws SAXException,
			IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);

		// optional, but recommended
		// read this -
		// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();

		return doc.getDocumentElement().getNodeName();
	}
}
