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
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameterSettings;
import org.apache.uima.util.Progress;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev
 * 
 */
public class FileDirectoryCollectionReader extends CollectionReader_ImplBase {

	private static final String PARAM_DIRECTORY_PATH = "DirectoryPath";
	private static final String PARAM_FILE_EXTENSION = "FileExtension";
	private static final String PARAM_ENCODING = "Encoding";
	private static final String DEFAULT_FILE_EXTENSION = "txt";
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
		ConfigurationParameterSettings cfg = getProcessingResourceMetaData()
				.getConfigurationParameterSettings();
		String directoryPath = (String) cfg.getParameterValue(PARAM_DIRECTORY_PATH);
		if (directoryPath == null) {
			throw new IllegalStateException("DirectoryPath param is NULL");
		}
		directory = new File(directoryPath);
		if (!directory.isDirectory()) {
			throw new IllegalStateException(String.format("%s is not existing file directory",
					directoryPath));
		}
		String fileExtension = (String) cfg.getParameterValue(PARAM_FILE_EXTENSION);
		if (fileExtension == null) {
			fileExtension = DEFAULT_FILE_EXTENSION;
		}
		IOFileFilter fileFilter = FileFilterUtils.suffixFileFilter(fileExtension);
		files = Lists.newArrayList(directory.listFiles((FileFilter) fileFilter));

		encoding = (String) cfg.getParameterValue(PARAM_ENCODING);
		if (encoding == null) {
			encoding = DEFAULT_ENCODING;
		}

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