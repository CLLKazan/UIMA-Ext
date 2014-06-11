/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.resource.ExternalResourceDescription;
import org.xml.sax.SAXException;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateTPSAnnotatorDescriptor {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		if (args.length != 1) {
			System.err.println("Provide modelBaseDir as the only argument!");
			System.exit(1);
		}
		File modelBaseDir = new File(args[0]);
		// TODO
		ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				CachedSerializedDictionaryResource.class, "file:dict.opcorpora.ser");
		File aggrDescFile = TieredPosSequenceAnnotatorFactory.createAggregateDescription(
				modelBaseDir, morphDictDesc, morphDictDesc);
		System.out.println("Produced: " + aggrDescFile);
	}

}
