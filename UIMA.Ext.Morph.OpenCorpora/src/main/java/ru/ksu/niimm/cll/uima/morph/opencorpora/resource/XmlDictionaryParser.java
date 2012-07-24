/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import static java.lang.System.currentTimeMillis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.PropertyConfigurator;
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

	public static MorphDictionaryImpl parse(InputStream in) throws IOException, SAXException,
			ParserConfigurationException {
		SAXParser xmlParser = SAXParserFactory.newInstance().newSAXParser();
		XMLReader xmlReader = xmlParser.getXMLReader();

		DictionaryXmlHandler dictHandler = new DictionaryXmlHandler();
		// LEMMA FILTERING
		dictHandler.addLemmaFilter(new LemmaByGrammemFilter("Surn", "Patr", "Orgn"));

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
		PropertyConfigurator.configure(XmlDictionaryParser.class.getClassLoader()
				.getResource("ru/ksu/niimm/cll/uima/morph/opencorpora/log4j.properties"));

		File dictXmlFile = new File(args[0]);
		if (!dictXmlFile.isFile()) {
			throw new IllegalStateException(dictXmlFile + " does not exist");
		}
		File outPath = new File(args[1]);
		outPath.getParentFile().mkdirs();

		MorphDictionaryImpl dict = parse(new FileInputStream(dictXmlFile));

		log.info("Preparing to serialization...");
		long timeBefore = currentTimeMillis();
		FileOutputStream fout = new FileOutputStream(outPath);
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