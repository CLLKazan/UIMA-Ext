package ru.kfu.itis.issst.corpus.statistics.cpe;

import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.util.Progress;
import org.uimafit.component.CasCollectionReader_ImplBase;

public class XmiFileTreeCollectionReader extends CasCollectionReader_ImplBase {

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

}
