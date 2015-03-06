/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import static org.apache.uima.fit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.CollectionReaderFactory;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev
 * 
 */
public class FileDirectoryCollectionReader extends CasCollectionReader_ImplBase {

	public static CollectionReaderDescription createDescription(File inputDir)
			throws ResourceInitializationException {
		TypeSystemDescription inputTSD = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem");
		return CollectionReaderFactory.createReaderDescription(
				FileDirectoryCollectionReader.class,
				inputTSD,
				PARAM_DIRECTORY_PATH, inputDir);
	}

	public static final String PARAM_DIRECTORY_PATH = "directoryPath";
	public static final String PARAM_FILE_EXTENSION = "fileExtension";
	public static final String PARAM_ENCODING = "encoding";
	public static final String PARAM_SET_RELATIVE_URI = "setRelativeURI";

	// config
	@ConfigurationParameter(name = PARAM_DIRECTORY_PATH, mandatory = true)
	private File directory;
	@ConfigurationParameter(name = PARAM_FILE_EXTENSION, defaultValue = "txt")
	private String fileExtension;
	@ConfigurationParameter(name = PARAM_ENCODING, defaultValue = "utf-8")
	private String encoding;
	@ConfigurationParameter(name = PARAM_SET_RELATIVE_URI, defaultValue = "true")
	private boolean setRelativeURI;
	// derived
	private ArrayList<File> files;
	// state
	private int lastReadFileIdx;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		if (!directory.isDirectory()) {
			throw new IllegalStateException(String.format(
					"%s is not existing file directory", directory));
		}
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(fileExtension);
		IOFileFilter subdirFilter = FileFilterUtils.trueFileFilter();
		files = Lists.newArrayList(FileUtils.listFiles(directory, fileFilter, subdirFilter));
		//
		lastReadFileIdx = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		if (!hasNext()) {
			throw new CollectionException(new NoSuchElementException());
		}
		final int curFileIdx = lastReadFileIdx + 1;
		File file = files.get(curFileIdx);
		lastReadFileIdx = curFileIdx;
		//
		String fileContent = FileUtils.readFileToString(file, encoding);
		aCAS.setDocumentText(fileContent);
		try {
			DocumentMetadata docMeta = new DocumentMetadata(aCAS.getJCas());
			docMeta.setSourceUri(getURIForMetadata(file).toString());
			docMeta.addToIndexes();
		} catch (CASException e) {
			throw new IllegalStateException(e);
		}
	}

	private URI getURIForMetadata(File f) {
		URI fURI = f.toURI();
		if (setRelativeURI) {
			URI dirURI = directory.toURI();
			URI resURI = dirURI.relativize(fURI);
			if (resURI.getScheme() == null) {
				resURI = URI.create("file:" + resURI.toString());
			}
			return resURI;
		} else {
			return fURI;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() throws IOException, CollectionException {
		return (lastReadFileIdx + 1) < files.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progress[] getProgress() {
		return new Progress[] {
				new ProgressImpl(lastReadFileIdx + 1, files.size(), Progress.ENTITIES)
		};
	}

}