/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static java.lang.System.currentTimeMillis;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class XmlDictionaryParser {

	private static final Logger log = LoggerFactory.getLogger(XmlDictionaryParser.class);

	/**
	 * Parse with default lemma post-processors
	 * 
	 * @param in
	 * @return {@link MorphDictionary} instance
	 */
	public static MorphDictionaryImpl parse(InputStream in) throws IOException, SAXException,
			ParserConfigurationException {
		return parse(in,
				new LemmaByGrammemFilter("Surn", "Patr", "Orgn"),
				YoLemmaPostProcessor.INSTANCE);
	}

	public static MorphDictionaryImpl parse(
			InputStream in, LemmaPostProcessor... lemmaPostProcessors)
			throws IOException, SAXException, ParserConfigurationException {
		SAXParser xmlParser = SAXParserFactory.newInstance().newSAXParser();
		XMLReader xmlReader = xmlParser.getXMLReader();

		DictionaryXmlHandler dictHandler = new DictionaryXmlHandler();
		for (LemmaPostProcessor lpp : lemmaPostProcessors) {
			dictHandler.addLemmaPostProcessor(lpp);
		}

		xmlReader.setContentHandler(dictHandler);
		InputSource xmlSource = new InputSource(in);
		log.info("About to parse xml dictionary file");
		long timeBefore = currentTimeMillis();
		xmlReader.parse(xmlSource);
		log.info("Parsing finished in {} ms", currentTimeMillis() - timeBefore);
		return dictHandler.getDictionary();
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: <xml-dictionary-file> <serialized-output-path>");
			return;
		}

		File dictXmlFile = new File(args[0]);
		if (!dictXmlFile.isFile()) {
			throw new IllegalStateException(dictXmlFile + " does not exist");
		}
		File outPath = new File(args[1]);
		outPath.getParentFile().mkdirs();

		FileInputStream fis = FileUtils.openInputStream(dictXmlFile);
		MorphDictionaryImpl dict;
		try {
			dict = parse(fis);
		} finally {
			IOUtils.closeQuietly(fis);
		}

		log.info("Preparing to serialization...");
		long timeBefore = currentTimeMillis();
		OutputStream fout = new BufferedOutputStream(
				new FileOutputStream(outPath), 8192 * 8);
		ObjectOutputStream out = new ObjectOutputStream(fout);
		try {
			out.writeObject(dict);
		} finally {
			out.close();
		}
		log.info("Serialization finished in {} ms.\nOutput size: {} bytes",
				currentTimeMillis() - timeBefore, outPath.length());
	}
}