package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ExternalResource;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.corpus.statistics.dao.corpus.CorpusDAO;
import ru.kfu.itis.issst.corpus.statistics.dao.corpus.UriAnnotatorPair;

public class CorpusDAOCollectionReader extends CasCollectionReader_ImplBase {

	public final static String CORPUS_DAO_KEY = "CorpusDAO";
	@ExternalResource(key = CORPUS_DAO_KEY)
	private CorpusDAO corpusDAO;

	public static final String SOURCE_DOCUMENT_INFORMATION_TYPE_NAME = "ru.kfu.itis.issst.corpus.statistics.type.SourceDocumentInformation";
	public static final String URI_FEAT_NAME = "uri";
	public static final String ANNOTATOR_ID_FEAT_NAME = "annotatorId";

	private Set<UriAnnotatorPair> uriAnnotatorPairs = new HashSet<UriAnnotatorPair>();
	private Iterator<UriAnnotatorPair> uriAnnotatorPairsIterator;
	private int mCurrentIndex;

	private Type sourceDocumentInformationType;
	private Feature uriFeature;
	private Feature annotatorIdFeature;

	@Override
	public void typeSystemInit(TypeSystem aTypeSystem) {
		sourceDocumentInformationType = aTypeSystem
				.getType(SOURCE_DOCUMENT_INFORMATION_TYPE_NAME);
		uriFeature = sourceDocumentInformationType
				.getFeatureByBaseName(URI_FEAT_NAME);
		annotatorIdFeature = sourceDocumentInformationType
				.getFeatureByBaseName(ANNOTATOR_ID_FEAT_NAME);
	}

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		try {
			for (URI document : corpusDAO.getDocuments()) {
				for (String annotatorId : corpusDAO.getAnnotatorIds(document)) {
					uriAnnotatorPairs.add(new UriAnnotatorPair(document,
							annotatorId));
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new ResourceInitializationException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResourceInitializationException();
		}

		uriAnnotatorPairsIterator = uriAnnotatorPairs.iterator();
		mCurrentIndex = 0;
	}

	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		UriAnnotatorPair pair = uriAnnotatorPairsIterator.next();
		try {
			corpusDAO
					.getDocumentCas(pair.getUri(), pair.getAnnotatorId(), aCAS);
		} catch (SAXException e) {
			getLogger().error(
					String.format("Exception at %s annotated by %s.",
							pair.getUri(), pair.getAnnotatorId()));
			e.printStackTrace();
			throw new CollectionException();
		}

		addSourceDocumentInformation(aCAS, pair);

		mCurrentIndex++;
	}

	private void addSourceDocumentInformation(CAS aCAS, UriAnnotatorPair pair) {
		FeatureStructure sourceDocumentInformation = aCAS
				.createFS(sourceDocumentInformationType);
		sourceDocumentInformation.setStringValue(uriFeature, pair.getUri()
				.toString());
		sourceDocumentInformation.setStringValue(annotatorIdFeature,
				pair.getAnnotatorId());
		aCAS.addFsToIndexes(sourceDocumentInformation);
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return uriAnnotatorPairsIterator.hasNext();
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(mCurrentIndex,
				uriAnnotatorPairs.size(), Progress.ENTITIES) };
	}

}
