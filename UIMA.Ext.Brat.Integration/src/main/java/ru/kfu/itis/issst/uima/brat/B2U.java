/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import java.io.File;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.itis.cll.uima.consumer.XmiWriter;
import ru.kfu.itis.cll.uima.cpe.CpeBuilder;
import ru.kfu.itis.cll.uima.cpe.ReportingStatusCallbackListener;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class B2U {

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: <typesystem-xml> <bratCorpusDir> <outputDir>");
			System.exit(1);
		}
		B2U instance = new B2U();
		instance.tsFile = new File(args[0]);
		instance.bratCorpusDir = new File(args[1]);
		instance.outputDir = new File(args[2]);
		instance.run();
	}

	private File bratCorpusDir;
	private File outputDir;
	private File tsFile;

	private B2U() {
	}

	private void run() throws Exception {
		CpeBuilder cpeBuilder = new CpeBuilder();
		cpeBuilder.setMaxProcessingUnitThreatCount(2);

		// make TypeSystemDesc
		TypeSystemDescription tsd = TypeSystemDescriptionFactory
				.createTypeSystemDescriptionFromPath(tsFile.toURI().toString());
		// configure CollectionReader
		CollectionReaderDescription colReaderDesc = CollectionReaderFactory.createDescription(
				BratCollectionReader.class, tsd,
				BratCollectionReader.PARAM_BRAT_COLLECTION_DIR, bratCorpusDir.getPath(),
				BratCollectionReader.PARAM_MAPPING_FACTORY_CLASS,
				AutoBratUimaMappingFactory.class.getName());
		cpeBuilder.setReader(colReaderDesc);
		// configure AE
		AnalysisEngineDescription aeDesc = createPrimitiveDescription(XmiWriter.class,
				XmiWriter.PARAM_OUTPUTDIR, outputDir.getPath());
		cpeBuilder.addAnalysisEngine(aeDesc);

		CollectionProcessingEngine cpe = cpeBuilder.createCpe();
		cpe.addStatusCallbackListener(new ReportingStatusCallbackListener(cpe));
		cpe.process();
	}
}
