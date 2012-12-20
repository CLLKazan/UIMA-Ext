/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import static org.apache.uima.collection.impl.metadata.cpe.CpeDescriptorFactory.produceCollectionReader;
import static org.apache.uima.collection.impl.metadata.cpe.CpeDescriptorFactory.produceDescriptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.impl.metadata.CpeDefaultValues;
import org.apache.uima.collection.impl.metadata.cpe.CpeDescriptionImpl;
import org.apache.uima.collection.impl.metadata.cpe.CpeDescriptorFactory;
import org.apache.uima.collection.metadata.CpeCollectionReader;
import org.apache.uima.collection.metadata.CpeComponentDescriptor;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.collection.metadata.CpeInclude;
import org.apache.uima.collection.metadata.CpeIntegratedCasProcessor;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;

/**
 * Build a Collection Processing Engine from a
 * {@link CollectionReaderDescription} and {@link AnalysisEngineDescription}s.
 * 
 * @author Richard Eckart de Castilho
 * @author Rinat Gareev (Kazan Federal University)
 */
public class CpeBuilder
{
	private static final String ACTION_ON_MAX_ERROR = "terminate";

	/**
	 * used for calculating the CAS pool size which needs to be adjusted to the
	 * number of parallel pipelines
	 */
	private int maxProcessingUnitThreatCount = 1;

	private int processorCounter = 1;

	private final CpeDescriptionImpl cpeDesc = (CpeDescriptionImpl) produceDescriptor();

	public void setMaxProcessingUnitThreatCount(int aMaxProcessingUnitThreatCount)
	{
		maxProcessingUnitThreatCount = aMaxProcessingUnitThreatCount;
	}

	public void setReader(CollectionReaderDescription aDesc)
			throws IOException, SAXException, CpeDescriptorException
	{
		// Remove all collection readers
		cpeDesc.setAllCollectionCollectionReaders(new CpeCollectionReader[0]);

		URL descUrl = materializeDescriptor(aDesc).toURI().toURL();
		CpeCollectionReader reader = produceCollectionReader(descUrl.toString());
		cpeDesc.addCollectionReader(reader);
	}

	public void addAnalysisEngine(AnalysisEngineDescription aDesc)
			throws IOException, SAXException, CpeDescriptorException, InvalidXMLException
	{
		CpeIntegratedCasProcessor proc = createProcessor("processor_" + (processorCounter++), aDesc);
		cpeDesc.addCasProcessor(proc);
	}

	public CollectionProcessingEngine createCpe()
			throws ResourceInitializationException, CpeDescriptorException
	{
		ResourceManager resMgr = UIMAFramework.newDefaultResourceManager();
		if (maxProcessingUnitThreatCount == 0) {
			cpeDesc.getCpeCasProcessors().setPoolSize(3);
		}
		else {
			cpeDesc.getCpeCasProcessors().setPoolSize(maxProcessingUnitThreatCount + 2);
			cpeDesc.setProcessingUnitThreadCount(maxProcessingUnitThreatCount);
		}
		CollectionProcessingEngine cpe = UIMAFramework.produceCollectionProcessingEngine(cpeDesc,
				resMgr, null);
		return cpe;
	}

	/**
	 * Writes a temporary file containing a xml descriptor of the given
	 * resource. Returns the file.
	 * 
	 * @param resource
	 *            A resource specifier that should we materialized.
	 * @return The file containing the xml representation of the given resource.
	 */
	private static File materializeDescriptor(ResourceSpecifier resource)
			throws IOException, SAXException
	{
		File tempDesc = File.createTempFile("desc", ".xml");
		tempDesc.deleteOnExit();

		BufferedWriter out = new BufferedWriter(new FileWriter(tempDesc));
		resource.toXML(out);
		out.close();

		return tempDesc;
	}

	private static CpeIntegratedCasProcessor createProcessor(String key,
			AnalysisEngineDescription aDesc)
			throws IOException, SAXException, CpeDescriptorException
	{
		URL descUrl = materializeDescriptor(aDesc).toURI().toURL();

		CpeInclude cpeInclude = UIMAFramework.getResourceSpecifierFactory().createInclude();
		cpeInclude.set(descUrl.toString());

		CpeComponentDescriptor ccd = UIMAFramework.getResourceSpecifierFactory().createDescriptor();
		ccd.setInclude(cpeInclude);

		CpeIntegratedCasProcessor proc = CpeDescriptorFactory.produceCasProcessor(key);
		proc.setCpeComponentDescriptor(ccd);
		proc.setAttributeValue(CpeDefaultValues.PROCESSING_UNIT_THREAD_COUNT, 1);
		proc.setActionOnMaxError(ACTION_ON_MAX_ERROR);
		proc.setMaxErrorCount(0);

		return proc;
	}
}