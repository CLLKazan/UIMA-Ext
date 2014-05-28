/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static java.lang.System.currentTimeMillis;
import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.util.InvalidXMLException;
import org.uimafit.factory.AnalysisEngineFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.util.PipelineDescriptorUtils;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ConfigurableSerializedDictionaryResource;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DummyWordformPredictor;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphLauncher {

	/**
	 * <b>NOTE!</b> uima.datapath system property must contain path to
	 * serialized dictionary!
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InvalidXMLException
	 */
	public static void main(String[] args) throws IOException, UIMAException {
		if (args.length != 2) {
			System.err.println("Usage: <input-file> <encoding>");
			return;
		}
		File inputFile = new File(args[0]);
		if (!inputFile.isFile()) {
			System.err.println("Specified file does not exist");
			return;
		}
		String encoding = args[1];
		File outputDir = inputFile.getParentFile();

		// configure logging
		System.setProperty("logback.configurationFile",
				"ru/ksu/niimm/cll/uima/morph/opencorpora/logback.xml");

		// configure dictionary description
		ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				ConfigurableSerializedDictionaryResource.class, "file:dict.opcorpora.ser",
				ConfigurableSerializedDictionaryResource.PARAM_PREDICTOR_CLASS_NAME,
				DummyWordformPredictor.class.getName());
		// configure AE
		Map<String, MetaDataObject> aeDescriptions = Maps.newLinkedHashMap();
		aeDescriptions.put("tokenizer", TokenizerAPI.getAEImport());
		aeDescriptions.put("morphAnalyzer", MorphologyAnnotator.createDescription(morphDictDesc));
		aeDescriptions
				.put("xmiWriter",
						createPrimitiveDescription(
								XmiWriter.class,
								createTypeSystemDescription("ru.kfu.itis.cll.uima.commons.Commons-TypeSystem"),
								XmiWriter.PARAM_OUTPUTDIR, outputDir.getPath()));
		AnalysisEngineDescription aeDesc = PipelineDescriptorUtils.createAggregateDescription(
				ImmutableList.copyOf(aeDescriptions.values()),
				ImmutableList.copyOf(aeDescriptions.keySet()));

		// create AE
		AnalysisEngine ae = AnalysisEngineFactory.createAggregate(aeDesc);

		// prepare input
		String inputText = FileUtils.readFileToString(inputFile, encoding);
		JCas cas = ae.newJCas();
		cas.setDocumentText(inputText);
		DocumentMetadata inputMeta = new DocumentMetadata(cas);
		inputMeta.setSourceUri(inputFile.toURI().toString());
		inputMeta.addToIndexes();

		// run
		long timeBefore = currentTimeMillis();
		ae.process(cas);
		System.out.println("Finished in " + (currentTimeMillis() - timeBefore) + " ms");
	}
}