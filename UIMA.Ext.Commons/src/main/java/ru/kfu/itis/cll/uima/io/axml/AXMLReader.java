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
 * An experimental tool to convert document text with annotations formatted in
 * simple XML schema:
 * <dl>
 * <dt> {@code <doc>}
 * <dd>A root element
 * <dt> {@code <meta>}
 * <dd>Attributes of this element is currently ignored; useful to give credits
 * to authors of a source text.
 * <dt> {@code <aliases>}
 * <dd>A block to define aliases (i.e., short labels) for annotation type names
 * that are used in {@code body}.
 * <dt> {@code <alias key="X_alias" type="FullyQualifiedNameOfX" />}
 * <dd>Defines a single alias.
 * <dt> {@code <body>}
 * <dd>Encloses a document text. Inside this element annotations are defined as:
 * <p>
 * {@code text before <X_alias>covered text</X_alias> text after}.
 * </p>
 * </dl>
 * 
 * It's primary purpose it to facilitate generation of test XMI files.
 * <p>
 * See example data in {@code test-data/} sub-folder of this module.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AXMLReader {

	/**
	 * Populate the specified CAS by a text and annotations from the specified
	 * input assuming that it is formatted as described above.
	 * 
	 * @param in
	 *            input Reader. It is a caller's responsibility to close this
	 *            reader instance.
	 * @param cas
	 *            CAS
	 * @throws IOException
	 * @throws SAXException
	 */
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
