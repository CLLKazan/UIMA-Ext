package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ExternalResource;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.corpus.statistics.dao.CorpusDAO;
import ru.kfu.itis.issst.corpus.statistics.dao.UriAnnotatorPair;

public class CorpusDAOCollectionReader extends CasCollectionReader_ImplBase {

	final static String CORPUS_DAO_KEY = "CorpusDAO";
	@ExternalResource(key = CORPUS_DAO_KEY)
	private CorpusDAO corpusDAO;

	private Set<UriAnnotatorPair> uriAnnotatorPairs;
	private Iterator<UriAnnotatorPair> uriAnnotatorPairsIterator;
	private int mCurrentIndex;

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
			e.printStackTrace();
			throw new CollectionException();
		}
		mCurrentIndex++;
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
