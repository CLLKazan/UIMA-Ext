/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev
 * 
 */
public class FileDirectoryCollectionReader extends CasCollectionReader_ImplBase {

	public static final String PARAM_DIRECTORY_PATH = "DirectoryPath";
	public static final String PARAM_FILE_EXTENSION = "FileExtension";
	public static final String PARAM_ENCODING = "Encoding";

	// config
	@ConfigurationParameter(name = PARAM_DIRECTORY_PATH, mandatory = true)
	private File directory;
	@ConfigurationParameter(name = PARAM_FILE_EXTENSION, defaultValue = "txt")
	private String fileExtension;
	@ConfigurationParameter(name = PARAM_ENCODING, defaultValue = "utf-8")
	private String encoding;
	// derived
	private List<File> files;
	// state
	private Iterator<File> fileIter;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		if (!directory.isDirectory()) {
			throw new IllegalStateException(String.format(
					"%s is not existing file directory", directory));
		}
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(fileExtension);
		files = Lists.newArrayList(directory.listFiles((FileFilter) fileFilter));

		fileIter = files.iterator();
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
		String fileContent = FileUtils.readFileToString(file, encoding);
		aCAS.setDocumentText(fileContent);
		try {
			DocumentMetadata docMeta = new DocumentMetadata(aCAS.getJCas());
			docMeta.setSourceUri(file.toURI().toString());
			docMeta.addToIndexes();
		} catch (CASException e) {
			throw new IllegalStateException(e);
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

}