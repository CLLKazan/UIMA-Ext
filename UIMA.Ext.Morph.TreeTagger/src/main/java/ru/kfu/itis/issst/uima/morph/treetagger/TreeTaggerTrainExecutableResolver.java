/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static java.io.File.separator;
import static org.annolab.tt4j.Util.getSearchPaths;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.annolab.tt4j.ExecutableResolver;
import org.annolab.tt4j.PlatformDetector;
import org.annolab.tt4j.Util;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TreeTaggerTrainExecutableResolver implements ExecutableResolver {

	protected PlatformDetector platformDetector;
	protected List<String> additionalPaths = new ArrayList<String>();

	public void destroy() {
		// Do nothing
	}

	/**
	 * Set additional paths that will be used for searching the
	 * TreeTagger-trainer executable.
	 * 
	 * @param aAdditionalPaths
	 *            list of additional paths.
	 * @see Util#getSearchPaths(List, String)
	 */
	public void setAdditionalPaths(final List<String> additionalPaths) {
		this.additionalPaths = additionalPaths;
	}

	public String getExecutable() throws IOException {
		Set<String> searchedIn = new HashSet<String>();
		for (final String p : getSearchPaths(additionalPaths, "bin")) {
			if (p == null) {
				continue;
			}

			final File exe = new File(p + separator + "train-tree-tagger"
					+ platformDetector.getExecutableSuffix());
			searchedIn.add(exe.getAbsolutePath());
			if (exe.exists()) {
				return exe.getAbsolutePath();
			}
		}

		throw new IOException(
				"Unable to locate train-tree-tagger binary in the following locations "
						+ searchedIn
						+ ". Make sure the environment variable 'TREETAGGER_HOME' or "
						+ "'TAGDIR' or the system property 'treetagger.home' point to the TreeTagger "
						+ "installation directory.");
	}

	/**
	 * Set platform information.
	 */
	public void setPlatformDetector(final PlatformDetector aPlatform) {
		platformDetector = aPlatform;
	}

}
