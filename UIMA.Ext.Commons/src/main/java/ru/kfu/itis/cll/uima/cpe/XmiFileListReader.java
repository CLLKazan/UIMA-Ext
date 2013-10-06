/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.uimafit.descriptor.ConfigurationParameter;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class XmiFileListReader extends XmiCollectionReaderBase {

	public static final String PARAM_LIST_FILE = "listFile";
	public static final String PARAM_BASE_DIR = "baseDir";

	@ConfigurationParameter(name = PARAM_BASE_DIR, mandatory = true)
	private String baseDirPath;
	@ConfigurationParameter(name = PARAM_LIST_FILE, mandatory = true)
	private File listFile;
	// derived
	private Resource baseDir;
	private List<Resource> resources;

	@Override
	protected Iterable<Resource> getResources(UimaContext ctx) throws IOException {
		String baseDirPath = this.baseDirPath;
		FilenameUtils.normalize(baseDirPath);
		// ensure that baseDirPath ends with slash for proper relative path handling
		if (!baseDirPath.endsWith(File.separator)) {
			baseDirPath += File.separator;
		}
		baseDir = new FileSystemResource(baseDirPath);
		List<String> lines = FileUtils.readLines(listFile, "utf-8");
		resources = Lists.transform(
				Lists.newArrayList(Iterables.filter(lines, notBlankString)),
				relativeFileResourceFunc);
		return resources;
	}

	@Override
	protected Integer getResourcesNumber() {
		return resources.size();
	}

	private static final Predicate<String> notBlankString = new Predicate<String>() {
		@Override
		public boolean apply(String arg) {
			return !StringUtils.isBlank(arg);
		}
	};

	private final Function<String, Resource> relativeFileResourceFunc = new Function<String, Resource>() {
		@Override
		public Resource apply(String relPath) {
			try {
				return baseDir.createRelative(relPath);
			} catch (IOException e) {
				// should never happen
				throw new IllegalStateException(e);
			}
		}
	};
}