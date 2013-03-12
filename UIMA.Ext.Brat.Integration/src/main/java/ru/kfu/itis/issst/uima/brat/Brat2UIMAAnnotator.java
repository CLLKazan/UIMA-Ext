package ru.kfu.itis.issst.uima.brat;

import java.io.File;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;



public class Brat2UIMAAnnotator extends CollectionReader_ImplBase {

	
public void initialize() throws ResourceInitializationException {
		
		

	
//		File directory = new File((String) getConfigParameterValue(PARAM_INPUTDIR));
//		mEncoding = (String) getConfigParameterValue(PARAM_ENCODING);
//		mDocumentTextXmlTagName = (String) getConfigParameterValue(PARAM_XMLTAG);
//		mLanguage = (String) getConfigParameterValue(PARAM_LANGUAGE);
//		mCurrentIndex = 0;
//
//		// get list of files (not subdirectories) in the specified directory
//		mFiles = new ArrayList();
//		File[] files = directory.listFiles();
//		for (int i = 0; i < files.length; i++) {
//			if (!files[i].isDirectory()) {
//				mFiles.add(files[i]);
//			}
//		}

	
}

	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
