/**
 * 
 */
package org.nlplab.brat.util;

import java.io.File;
import java.io.FileFilter;

import org.nlplab.brat.BratConstants;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class BratCorpusFileFilter implements FileFilter {

	public static final BratCorpusFileFilter INSTANCE = new BratCorpusFileFilter();

	private BratCorpusFileFilter() {
	}

	@Override
	public boolean accept(File f) {
		if (!f.isDirectory()) {
			return false;
		}
		File annoConfFile = new File(f, BratConstants.ANNOTATION_CONF_FILE);
		return annoConfFile.isFile();
	}

}
