package ru.kfu.itis.issst.corpus.statistics.app;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.uima.collection.metadata.CpeDescriptorException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

public class XmiCorpusUnitsExtractorTest {

	String corpusPathString = Thread.currentThread().getContextClassLoader()
			.getResource("corpus_example").getPath();

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void test() throws ResourceInitializationException,
			InvalidXMLException, IOException, SAXException,
			CpeDescriptorException, ParserConfigurationException {
		XmiCorpusUnitsExtractor.main(new String[] { "-corpus",
				corpusPathString, "-unit",
				"ru.kfu.cll.uima.tokenizer.fstype.W", "-class",
				"ru.kfu.itis.issst.evex.Person", "-class",
				"ru.kfu.itis.issst.evex.Organization", "-class",
				"ru.kfu.itis.issst.evex.Weapon", "-output",
				tempFolder.newFile().getPath() });
	}

}
