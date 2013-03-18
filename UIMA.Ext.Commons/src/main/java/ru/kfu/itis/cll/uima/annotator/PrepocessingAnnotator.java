package ru.kfu.itis.cll.uima.annotator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.commons.MisisDocumentMetadata;

public class PrepocessingAnnotator extends CasAnnotator_ImplBase {

	@Override
	public void process(CAS aCAS) throws AnalysisEngineProcessException {
		{
			
			if(aCAS.getDocumentLanguage() != null)
			{
		
			try {
				DocumentParser dp = new DocumentParser();
					
					InputStream stream = new ByteArrayInputStream(aCAS
							.getDocumentText().getBytes());

					dp.MainText =  aCAS.getDocumentText();
							
					String mimeType = aCAS.getView("misisDocumentSofa").getSofaMimeType();

					dp.analyse(stream, mimeType);					
					
//					System.out.println(aCAS.getView("misisDocumentSofa").getSofaDataURI() + " " + aCAS.getView("misisDocumentSofa").getSofaMimeType());
																											
					MisisDocumentMetadata docMeta = new MisisDocumentMetadata(aCAS.getJCas());
					
					
					docMeta.setLanguage(dp.Language);
					docMeta.setDocumentRawText(dp.MainText);
					docMeta.setFormat(dp.Format);
					docMeta.setSourceUri(aCAS.getView("misisDocumentSofa").getSofaDataURI());
					docMeta.addToIndexes(aCAS.getJCas());
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CASException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}

			
		}
	}

	// class for parsing documents
	private class DocumentParser {
		private String Language = "";
		private String MainText = "";
		private String Format = "";

		public void analyse(InputStream stream, String mimeType)
				throws IOException, SAXException {

			if (mimeType.contains(".pdf")) {
				Format = "pdf";
//				parsing(new PDFParser(), stream);
			} else if (mimeType.contains(".doc")) {
				Format = "doc";
//				parsing(new OfficeParser(), stream);
			} else if (mimeType.contains(".xls")) {
				Format = "xls";
//				parsing(new OfficeParser(), stream);
			} else if (mimeType.contains(".txt")) {
				Format = "txt";
//				parsing(new TXTParser(), stream);
			} else {
				if (mimeType.contains(".htm") || mimeType.contains(".html")) {
					Format = "html";
//					parsing(new HtmlParser(), stream);
				}
				/* throw new IllegalStateException("Invalid extension."); */
			}						 
			LanguageIdentifier langid = new LanguageIdentifier(MainText);
			Language = langid.getLanguage();
		}		
	}

}
