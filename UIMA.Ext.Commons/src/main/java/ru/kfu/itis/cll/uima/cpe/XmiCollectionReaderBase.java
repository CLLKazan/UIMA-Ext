package ru.kfu.itis.cll.uima.cpe;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.springframework.core.io.Resource;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev
 */
public abstract class XmiCollectionReaderBase extends CasCollectionReader_ImplBase {

	/**
	 * Name of the configuration parameter that must be set to indicate if the
	 * execution fails if an encountered type is unknown
	 */
	public static final String PARAM_FAILUNKNOWN = "FailOnUnknownType";

	@ConfigurationParameter(name = PARAM_FAILUNKNOWN, defaultValue = "true", mandatory = false)
	private Boolean mFailOnUnknownType = true;

	// state fields
	private Iterable<Resource> resources;
	private Iterator<Resource> resourcesIter;
	private Integer resourcesNum;
	private int resourcesRead;

	public void initialize(final UimaContext ctx) throws ResourceInitializationException {
		resourcesRead = 0;
		try {
			resources = getResources(ctx);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		resourcesIter = resources.iterator();
		resourcesNum = getResourcesNumber();
	}

	public boolean hasNext() {
		return resourcesIter.hasNext();
	}

	public void getNext(CAS aCAS) throws IOException, CollectionException {
		Resource currentRes = resourcesIter.next();
		resourcesRead++;
		InputStream inputStream = currentRes.getInputStream();
		try {
			XmiCasDeserializer.deserialize(inputStream, aCAS, !mFailOnUnknownType);
		} catch (SAXException e) {
			throw new CollectionException(e);
		} finally {
			inputStream.close();
		}
	}

	public Progress[] getProgress() {
		int total = resourcesNum == null ? -1 : resourcesNum;
		return new Progress[] { new ProgressImpl(resourcesRead, total, Progress.ENTITIES) };
	}

	protected abstract Iterable<Resource> getResources(UimaContext ctx)
			throws IOException, ResourceInitializationException;

	protected abstract Integer getResourcesNumber();
}