/**
 * 
 */
package ru.kfu.itis.cll.uima.eval.cas;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

/**
 * A {@link CasDirectory} implementation that read only files from the specified
 * 'dir' that have names from the specified 'listFile'.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FileListCasDirectory extends FSCasDirectory {

	// config fields
	private File listFile;

	@PostConstruct
	@Override
	public void init() {
		super.init();
		listFile = env.getProperty(beanName + ".listFile", File.class);
		if (listFile == null) {
			throw new IllegalStateException("listFile property value is not specified");
		}
		if (!listFile.isFile()) {
			throw new IllegalStateException(String.format(
					"%s is not an existing file", listFile));
		}
	}

	@Override
	protected FileFilter getSourceFileFilter() {
		final Set<String> included = Sets.newHashSet();
		List<String> includedSrcList;
		try {
			includedSrcList = FileUtils.readLines(listFile, "utf-8");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		for (String p : includedSrcList) {
			if (StringUtils.isBlank(p)) {
				continue;
			}
			File pFile = new File(p);
			if (pFile.isAbsolute()) {
				included.add(pFile.getAbsolutePath());
			} else {
				included.add(new File(dir, p).getAbsolutePath());
			}
		}
		return new FileFilter() {
			@Override
			public boolean accept(File f) {
				return included.contains(f.getAbsolutePath());
			}
		};
	}
	/*
	 * TODO fix getCAS(URI uri)
	 */
}