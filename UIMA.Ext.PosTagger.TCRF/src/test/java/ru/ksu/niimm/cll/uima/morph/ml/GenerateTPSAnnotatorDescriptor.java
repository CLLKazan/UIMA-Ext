/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.resource.ExternalResourceDescription;
import org.xml.sax.SAXException;

import ru.kfu.itis.issst.uima.morph.dictionary.MorphDictionaryAPIFactory;

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
		ExternalResourceDescription morphDictDesc = MorphDictionaryAPIFactory
				.getMorphDictionaryAPI()
				.getResourceDescriptionForCachedInstance();
		File aggrDescFile = TieredPosSequenceAnnotatorFactory.createAggregateDescription(
				modelBaseDir, morphDictDesc, morphDictDesc);
		System.out.println("Produced: " + aggrDescFile);
	}

}
