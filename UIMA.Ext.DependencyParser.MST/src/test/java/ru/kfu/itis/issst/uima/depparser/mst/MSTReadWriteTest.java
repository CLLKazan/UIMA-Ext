package ru.kfu.itis.issst.uima.depparser.mst;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.junit.Assert;
import org.junit.Test;
import org.uimafit.pipeline.SimplePipeline;

import ru.kfu.itis.issst.uima.depparser.mst.MSTCollectionReader;
import ru.kfu.itis.issst.uima.depparser.mst.MSTWriter;

public class MSTReadWriteTest {

	@Test
	public void testOnNotEmptyFile() throws UIMAException, IOException {
		File inputFile = new File("src/test/resources/mst-example.txt");
		File outputFile = new File("target/mst-example.txt");
		//
		CollectionReaderDescription colReaderDesc = MSTCollectionReader
				.createDescription(inputFile);
		AnalysisEngineDescription writerDesc = MSTWriter.createDescription(outputFile);
		SimplePipeline.runPipeline(colReaderDesc, writerDesc);
		//
		List<String> expectedLines = FileUtils.readLines(inputFile, "UTF-8");
		List<String> actualLines = FileUtils.readLines(outputFile, "UTF-8");
		// remove trailing empty line
		// actualLines.remove(actualLines.get(actualLines.size() - 1));
		//
		Assert.assertEquals(expectedLines, actualLines);
	}
}