/**
 * 
 */
package ru.kfu.itis.cll.uima.io.axml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import ru.kfu.itis.cll.uima.io.IoUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AXMLReader {

	public static void read(Reader in, CAS cas) throws IOException, SAXException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		AXMLContentHandler contentHandler = new AXMLContentHandler(cas.getTypeSystem());
		xmlReader.setContentHandler(contentHandler);
		InputSource inputSource = new InputSource(in);
		xmlReader.parse(inputSource);
		cas.setDocumentText(contentHandler.getText());
		for (Annotation _anno : contentHandler.getAnnotations()) {
			String typeName = _anno.getType();
			Type type = cas.getTypeSystem().getType(typeName);
			if (type == null) {
				throw new IllegalStateException(String.format("Unknown type: %s", typeName));
			}
			AnnotationFS anno = cas.createAnnotation(type, _anno.getBegin(), _anno.getEnd());
			cas.addFsToIndexes(anno);
		}
	}

	public static void read(File file, CAS cas) throws IOException, SAXException {
		BufferedReader in = IoUtils.openReader(file);
		try {
			read(in, cas);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private AXMLReader() {
	}
}
