package ru.kfu.itis.cll.uima.cpe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/*import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;*/
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

import org.apache.tika.exception.TikaException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

import ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata;

public class PreprocessingCollectionReader extends CollectionReader_ImplBase {

	

	private static final String PARAM_DIRECTORY_PATH = "DirectoryPath";
//	private static final String PARAM_FILE_EXTENSION = "FileExtension";
	private static final String PARAM_ENCODING = "Encoding";
//	private static final String DEFAULT_FILE_EXTENSION = "txt";
	private static final String DEFAULT_ENCODING = "utf-8";
	
	// config
	private File directory;
	private List<File> files;
	private String encoding;
	// state
	private Iterator<File> fileIter;
		
	@Override
	public void initialize() throws ResourceInitializationException {
		super.initialize();
		UimaContext ctx = getUimaContext();
//		String directoryPath = (String) ctx.getConfigParameterValue(PARAM_DIRECTORY_PATH);
//		if (directoryPath == null) {
//			throw new IllegalStateException("DirectoryPath param is NULL");
//		}
//		directory = new File(directoryPath);
//		if (!directory.isDirectory()) {
//			throw new IllegalStateException(String.format("%s is not existing file directory",
//					directoryPath));
//		}
		/*String fileExtension = (String) ctx.getConfigParameterValue(PARAM_FILE_EXTENSION);
		if (fileExtension == null) {
			fileExtension = DEFAULT_FILE_EXTENSION;
		}*/
		/*IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(fileExtension);*/
//		files = Lists.newArrayList(directory.listFiles(/*(FileFilter) fileFilter)*/));
		encoding = (String) ctx.getConfigParameterValue(PARAM_ENCODING);
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		}

//		fileIter = files.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		if (!hasNext()) {
			throw new CollectionException(new NoSuchElementException());
		}
		
		
		File file = fileIter.next();
		if(! file.isDirectory())
		{
		try {
			DocumentParser dp = new DocumentParser();
			
			dp.analyse(file);
		System.out.println(dp.Format);
		String fileContent = dp.MainText;
		aCAS.setDocumentText(fileContent);
		try {
			MisisDocumentMetadata docMeta = new MisisDocumentMetadata(aCAS.getJCas());
			docMeta.setSourceUri(file.toURI().toString());
			docMeta.setDocumentRawText(dp.MainText);
			docMeta.setLanguage(dp.Language);
			docMeta.setFormat(dp.Format);
			docMeta.addToIndexes();
			System.out.println(docMeta.getSourceUri() +" " + docMeta.getLanguage()+" " + docMeta.getFormat() );
		} catch (CASException e) {
			throw new IllegalStateException(e);
		}
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return fileIter.hasNext();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progress[] getProgress() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
	}
	// class for parsing documents
	private  class DocumentParser {
		private  String Language = "";
		private  String MainText = "";
		private  String Format = "";
		public  void analyse(File file) throws IOException, SAXException
		{
			
			if (file.getName().endsWith(".pdf"))
			{
				Format = "pdf";
				parsing(new PDFParser(), file);
			}
			else
				if (file.getName().endsWith(".doc"))
				{
					Format = "doc";
					parsing(new OfficeParser(), file);
				}
				else
					if (file.getName().endsWith(".xls"))
					{
						Format = "xls";
						parsing(new OfficeParser(), file);
					}
					else
						if (file.getName().endsWith(".txt"))
						{
							Format = "txt";
							parsing(new TXTParser(), file);
						}
						else
						{
							if (file.getName().endsWith(".htm") || file.getName().endsWith(".html") )
							{
								Format = "html";
								parsing(new HtmlParser(), file);
							}
							/*throw new IllegalStateException("Invalid extension.");*/
						}
			LanguageIdentifier langid = new LanguageIdentifier(MainText);
			Language = langid.getLanguage();		
		}	
		private  void parsing(AbstractParser parser, File file) throws IOException, SAXException
		{
			FileInputStream stream = new FileInputStream(file);
			BodyContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			try {
				parser.parse(stream, handler, metadata);
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			MainText = new String(outstream.toByteArray());
		}
	}
}
