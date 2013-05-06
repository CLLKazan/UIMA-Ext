/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import static org.uimafit.factory.CollectionReaderFactory.createDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.xml.sax.SAXException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateCollectionReaderDescriptor {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		String outputPath = "src/main/resources/ru/kfu/itis/cll/uima/cpe/FileDirectoryCollectionReader.xml";
		TypeSystemDescription tsd = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem");
		CollectionReaderDescription crDesc = createDescription(
				FileDirectoryCollectionReader.class,
				tsd);

		FileOutputStream out = new FileOutputStream(outputPath);
		try {
			crDesc.toXML(out);
		} finally {
			out.close();
		}
	}
}