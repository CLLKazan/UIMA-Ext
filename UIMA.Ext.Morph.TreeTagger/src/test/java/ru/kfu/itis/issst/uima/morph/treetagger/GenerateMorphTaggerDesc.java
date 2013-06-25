/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateMorphTaggerDesc {

	public static void main(String[] args) throws Exception {
		String outPath = "src/main/resources/" +
				"ru/kfu/itis/issst/uima/morph/treetagger/MorphTagger.xml";
		TypeSystemDescription tsDesc = createTypeSystemDescription("org.opencorpora.morphology-ts");
		AnalysisEngineDescription desc = createPrimitiveDescription(MorphTagger.class, tsDesc);
		File outFile = new File(outPath);
		{
			outFile.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(outFile);
		try {
			desc.toXML(fos);
		} finally {
			fos.close();
		}
	}

}