/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

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
public class CpeLauncher {

	public static void main(String[] args) throws IOException, UIMAException,
			CpeDescriptorException {
		if (args.length != 1) {
			System.err.println("Usage: <cpeDescriptorPath>");
			return;
		}
		String cpeDescPath = args[0];

		XMLInputSource cpeDescSource = new XMLInputSource(cpeDescPath);
		CpeDescription cpeDesc = UIMAFramework.getXMLParser().parseCpeDescription(cpeDescSource);

		// produce
		CollectionProcessingEngine cpe = UIMAFramework
				.produceCollectionProcessingEngine(cpeDesc);
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe));

		// run
		cpe.process();
	}
}