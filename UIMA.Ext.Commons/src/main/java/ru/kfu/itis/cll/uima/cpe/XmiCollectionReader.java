package ru.kfu.itis.cll.uima.cpe;

import static org.apache.commons.io.filefilter.FileFilterUtils.suffixFileFilter;
import static org.apache.commons.io.filefilter.FileFilterUtils.trueFileFilter;
import static ru.kfu.itis.cll.uima.cpe.PUtils.file2Resource;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.core.io.Resource;
import org.uimafit.descriptor.ConfigurationParameter;

import com.google.common.collect.Collections2;

/**
 * A simple collection reader that reads CASes in XMI format from a directory
 * and its subdirectories in the filesystem.
 */
public class XmiCollectionReader extends XmiCollectionReaderBase {
	/**
	 * Name of configuration parameter that must be set to the path of a
	 * directory containing the XMI files.
	 */
	public static final String PARAM_INPUTDIR = "InputDirectory";

	@ConfigurationParameter(name = PARAM_INPUTDIR, mandatory = true)
	private File inputDir;

	// derived
	private Collection<Resource> xmiResources;

	@Override
	protected Iterable<Resource> getResources(UimaContext ctx) throws IOException,
			ResourceInitializationException {
		// if input directory does not exist or is not a directory, throw exception
		if (!inputDir.isDirectory()) {
			throw new ResourceInitializationException(
					ResourceConfigurationException.DIRECTORY_NOT_FOUND,
					new Object[] { PARAM_INPUTDIR, this.getMetaData().getName(),
							inputDir.getPath() });
		}
		// get list of .xmi files in the specified directory
		Collection<File> mFiles = FileUtils.listFiles(inputDir,
				suffixFileFilter(".xmi"), trueFileFilter());
		xmiResources = Collections2.transform(mFiles, file2Resource);
		return xmiResources;
	}

	@Override
	protected Integer getResourcesNumber() {
		return xmiResources.size();
	}

}