/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;

import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class GenerateMorphTaggerDesc {

	public static void main(String[] args) throws Exception {
		String outPath = "src/main/resources/" +
				"ru/kfu/itis/issst/uima/morph/treetagger/MorphTagger.xml";
		AnalysisEngineDescription desc = createEngineDescription(MorphTagger.class,
				PosTaggerAPI.getTypeSystemDescription());
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