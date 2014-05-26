/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import java.io.FileOutputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateTypeSystem4CasViewer {

	public static void main(String[] args) throws Exception {
		String outputPath = "src/test/resources/opencorpora/aggregate-4CasEditor-TS.xml";
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				TokenizerAPI.TYPESYSTEM_TOKENIZER,
				"ru.kfu.itis.issst.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		tsd = CasCreationUtils.mergeTypeSystems(Arrays.asList(tsd));
		FileOutputStream os = new FileOutputStream(outputPath);
		try {
			tsd.toXML(os);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

}