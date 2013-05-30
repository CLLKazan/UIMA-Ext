/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.component.JCasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import ru.kfu.cll.uima.segmentation.fstype.Paragraph;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.CAP;
import ru.kfu.cll.uima.tokenizer.fstype.CW;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.SPECIAL;
import ru.kfu.cll.uima.tokenizer.fstype.SW;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class RusCorporaCollectionReader extends JCasCollectionReader_ImplBase {

	public static final String PARAM_INPUT_DIR = "InputDirectory";
	public static final String PARAM_TAG_MAPPER_CLASS = "TagMapperClass";
	private static final String DEFAULT_INPUT_FILE_EXT = ".xhtml";

	@ConfigurationParameter(name = PARAM_INPUT_DIR, mandatory = true)
	private File inputDir;
	@ConfigurationParameter(name = PARAM_TAG_MAPPER_CLASS, mandatory = true,
			defaultValue = "ru.ksu.niimm.cll.uima.morph.ruscorpora.RusCorpora2OpenCorporaTagMapper")
	private String tagMapperClassName;
	// derived
	private List<File> inputFiles;
	private RusCorporaTagMapper tagMapper;
	// state fields
	private XMLReader xmlReader;
	private int lastReadFileIndex = -1;
	private String curFileName;
	private int wordCounter;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		try {
			@SuppressWarnings("unchecked")
			Class<RusCorporaTagMapper> tagMapperClass = (Class<RusCorporaTagMapper>)
					Class.forName(tagMapperClassName);
			tagMapper = tagMapperClass.newInstance();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
		if (!inputDir.isDirectory()) {
			throw new IllegalArgumentException(String.format(
					"%s is not existing directory", inputDir));
		}
		String inputFileExt = DEFAULT_INPUT_FILE_EXT;
		inputFiles = ImmutableList.copyOf(
				inputDir.listFiles((FileFilter)
						FileFilterUtils.suffixFileFilter(inputFileExt)));
		getLogger().info(String.format("Detected *%s files in %s",
				inputFileExt, inputFiles.size()));
		try {
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			xmlReader = saxParser.getXMLReader();
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return lastReadFileIndex < inputFiles.size() - 1;
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		File inputFile = inputFiles.get(++lastReadFileIndex);
		curFileName = inputFile.getName();
		InputStream is = new BufferedInputStream(new FileInputStream(inputFile));
		RusCorporaXmlContentHandler xmlHandler = new RusCorporaXmlContentHandler();
		try {
			InputSource xmlSource = new InputSource(is);
			xmlReader.setContentHandler(xmlHandler);
			xmlReader.parse(xmlSource);
		} catch (Exception e) {
			throw new CollectionException(new IllegalStateException(
					"Parsing " + curFileName + "...", e));
		} finally {
			IOUtils.closeQuietly(is);
		}
		String docText = xmlHandler.getDocumentText();
		jCas.setDocumentText(docText);
		// set document meta
		DocumentMetadata docMeta = new DocumentMetadata(jCas, 0, 0);
		docMeta.setDocumentSize(docText.length());
		docMeta.setSourceUri(inputFile.toURI().toString());
		docMeta.addToIndexes();
		// add paragraphs
		for (RusCorporaAnnotation para : xmlHandler.getParagraphs()) {
			new Paragraph(jCas, para.getBegin(), para.getEnd()).addToIndexes();
		}
		// add sentences
		for (RusCorporaAnnotation sent : xmlHandler.getSentences()) {
			new Sentence(jCas, sent.getBegin(), sent.getEnd()).addToIndexes();
		}
		// add wordforms
		for (RusCorporaWordform srcWf : xmlHandler.getWordforms()) {
			int wBegin = srcWf.getBegin();
			int wEnd = srcWf.getEnd();
			Word w = new Word(jCas, wBegin, wEnd);
			// make token anno
			Token tok = makeToken(jCas,
					docText.substring(wBegin, wEnd),
					wBegin, wEnd);
			tok.addToIndexes();
			w.setToken(tok);
			// make wordform anno 
			Wordform wf = new Wordform(jCas);
			tagMapper.mapFromRusCorpora(srcWf, wf);
			wf.setWord(w);
			w.setWordforms(FSUtils.toFSArray(jCas, wf));
			w.addToIndexes();
			wordCounter++;
		}
		// clear per-CAS state
		curFileName = null;
	}

	private Token makeToken(JCas jCas, String str, int begin, int end) {
		if (str.isEmpty()) {
			throw new IllegalStateException(String.format(
					"Empty token (%s,%s) in %s", begin, end, curFileName));
		}
		// search for letter
		int firstLetterIdx = -1;
		for (int i = 0; i < str.length(); i++) {
			if (Character.isLetter(str.charAt(i))) {
				firstLetterIdx = i;
				break;
			}
		}
		if (firstLetterIdx < 0) {
			// no letters, search for digit
			boolean hasDigit = false;
			for (int i = 0; i < str.length() && !hasDigit; i++) {
				if (Character.isDigit(str.charAt(i))) {
					hasDigit = true;
				}
			}
			if (hasDigit) {
				return new NUM(jCas, begin, end);
			} else {
				getLogger().info(String.format("SPECIAL detected in %s: %s",
						curFileName, str));
				return new SPECIAL(jCas, begin, end);
			}
		}
		char firstLetter = str.charAt(firstLetterIdx);
		if (Character.isUpperCase(firstLetter)) {
			// check for CAP
			boolean allCap = true;
			for (int i = 0; i < str.length() && allCap; i++) {
				char ch = str.charAt(i);
				if (Character.isLetter(ch) && !Character.isUpperCase(ch)) {
					allCap = false;
				}
			}
			return allCap ? new CAP(jCas, begin, end) : new CW(jCas, begin, end);
		} else {
			return new SW(jCas, begin, end);
		}
	}

	@Override
	public Progress[] getProgress() {
		int filesRead = lastReadFileIndex + 1;
		return new Progress[] {
				new ProgressImpl(filesRead, inputFiles.size(), Progress.ENTITIES)
		};
	}

	@Override
	public void close() throws IOException {
		getLogger().info(String.format(
				"Words parsed: %s", wordCounter));
		super.close();
	}
}