/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.metadata.CpeDescription;
import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.util.XMLInputSource;

import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TxtToXmiLauncher {

	public static void main(String[] args) throws IOException, UIMAException,
			CpeDescriptorException {
		if (args.length != 2) {
			System.err.println("Usage: <txtInputDir> <xmiOutputDir>");
			return;
		}
		String cpeDescPath = "desc/cpe/cpe-txt-to-xmi.xml";
		String inputDirPath = args[0];
		String outputDirPath = args[1];

		XMLInputSource cpeDescSource = new XMLInputSource(cpeDescPath);
		CpeDescription cpeDesc = UIMAFramework.getXMLParser().parseCpeDescription(cpeDescSource);

		// configure reader
		cpeDesc.getAllCollectionCollectionReaders()[0]
				.getConfigurationParameterSettings().setParameterValue("DirectoryPath",
						inputDirPath);

		// configure writer
		cpeDesc.getCpeCasProcessors().getAllCpeCasProcessors()[0]
				.getConfigurationParameterSettings()
				.setParameterValue("XmiOutputDir", outputDirPath);

		// produce
		CollectionProcessingEngine cpe = UIMAFramework
				.produceCollectionProcessingEngine(cpeDesc);
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe));

		// run
		cpe.process();
	}
}