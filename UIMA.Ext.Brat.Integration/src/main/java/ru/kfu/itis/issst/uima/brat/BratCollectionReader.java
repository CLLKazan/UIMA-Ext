package ru.kfu.itis.issst.uima.brat;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import static ru.kfu.itis.issst.uima.brat.BratConstants.*;

/**
 * Brat 2 UIMA Annotator is CAS Annotator to convert Brat standoff format
 * annotations to UIMA annotations. 1) defines input, ouput files directories of
 * .txt and .ann files 2) reading files and process its content using specified
 * file name parameter in DocumentMetadata annotations. 4) reading Brat
 * annotations and converts them to UIMA annotation (*.xmi files) T: text-bound
 * annotation R: relation E: event A: attribute M: modification (alias for
 * attribute, for backward compatibility) N: normalization #: note
 * 
 * @author RGareev (Kazan Federal University)
 * @author pathfinder
 */

public class BratCollectionReader extends CasCollectionReader_ImplBase {

	public static final String PARAM_BRAT_COLLECTION_DIR = "BratCollectionDir";

	@ConfigurationParameter(name = PARAM_BRAT_COLLECTION_DIR, mandatory = true)
	private File bratCollectionDir;

	// state fields
	private Iterator<BratDocument> bratDocIter;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		// TODO make bratDocIter 
	}

	@Override
	public void typeSystemInit(TypeSystem ts) throws ResourceInitializationException {
		// TODO Auto-generated method stub
		super.typeSystemInit(ts);
	}

	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return bratDocIter.hasNext();
	}

	@Override
	public void getNext(CAS cas) throws IOException, CollectionException {
		BratDocument bratDoc = bratDocIter.next();
		// read and set text
		String txt = FileUtils.readFileToString(bratDoc.getTxtFile(), TXT_FILES_ENCODING);
		cas.setDocumentText(txt);
		// read Brat annotations
	}

	@Override
	public Progress[] getProgress() {
		// TODO Auto-generated method stub
		return null;
	}
}