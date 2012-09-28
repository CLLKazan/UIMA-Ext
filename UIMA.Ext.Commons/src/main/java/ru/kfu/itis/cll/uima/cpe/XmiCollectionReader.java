package ru.kfu.itis.cll.uima.cpe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.uimafit.component.CasCollectionReader_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * A simple collection reader that reads CASes in XMI format from a directory in
 * the filesystem.
 */
public class XmiCollectionReader extends CasCollectionReader_ImplBase {
	/**
	 * Name of configuration parameter that must be set to the path of a
	 * directory containing the XMI files.
	 */
	public static final String PARAM_INPUTDIR = "InputDirectory";

	/**
	 * Name of the configuration parameter that must be set to indicate if the
	 * execution fails if an encountered type is unknown
	 */
	public static final String PARAM_FAILUNKNOWN = "FailOnUnknownType";

	@ConfigurationParameter(name = PARAM_FAILUNKNOWN, defaultValue = "true")
	private Boolean mFailOnUnknownType = true;

	@ConfigurationParameter(name = PARAM_INPUTDIR, mandatory = true)
	private File inputDir;

	// derived
	private List<File> mFiles;

	// state fields
	private int mCurrentIndex;

	public void initialize(final UimaContext ctx) throws ResourceInitializationException {
		mCurrentIndex = 0;

		// if input directory does not exist or is not a directory, throw exception
		if (!inputDir.exists() || !inputDir.isDirectory()) {
			throw new ResourceInitializationException(
					ResourceConfigurationException.DIRECTORY_NOT_FOUND,
					new Object[] { PARAM_INPUTDIR, this.getMetaData().getName(),
							inputDir.getPath() });
		}

		// get list of .xmi files in the specified directory
		File[] files = inputDir.listFiles();
		mFiles = Lists.newArrayListWithExpectedSize(files.length);
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory() && files[i].getName().endsWith(".xmi")) {
				mFiles.add(files[i]);
			}
		}
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#hasNext()
	 */
	public boolean hasNext() {
		return mCurrentIndex < mFiles.size();
	}

	/**
	 * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
	 */
	public void getNext(CAS aCAS) throws IOException, CollectionException {
		File currentFile = (File) mFiles.get(mCurrentIndex++);
		FileInputStream inputStream = new FileInputStream(currentFile);
		try {
			XmiCasDeserializer.deserialize(inputStream, aCAS, !mFailOnUnknownType);
		} catch (SAXException e) {
			throw new CollectionException(e);
		} finally {
			inputStream.close();
		}
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
	 */
	public void close() throws IOException {
	}

	/**
	 * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
	 */
	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(mCurrentIndex, mFiles.size(), Progress.ENTITIES) };
	}

}