/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.annolab.tt4j.ExecutableResolver;
import org.annolab.tt4j.PlatformDetector;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultHunposExecutableResolver implements ExecutableResolver {

	public static final String HUNPOS_TAGGER_FILE_BASENAME = "hunpos-tag";

	protected PlatformDetector platformDetector;

	@Override
	public void setPlatformDetector(PlatformDetector platformDetector) {
		this.platformDetector = platformDetector;
	}

	@Override
	public void destroy() {
		// do nothing
	}

	@Override
	public String getExecutable() throws IOException {
		Set<String> searchedIn = new HashSet<String>();
		for (String p : getSearchPaths()) {
			if (p == null) {
				continue;
			}

			File exe = new File(p + separator + HUNPOS_TAGGER_FILE_BASENAME
					+ platformDetector.getExecutableSuffix());
			searchedIn.add(exe.getAbsolutePath());
			if (exe.exists()) {
				return exe.getAbsolutePath();
			}
		}
		throw new IOException(
				"Unable to locate hunpos tagger binary in the following locations "
						+ searchedIn
						+ ". Make sure the environment variable 'HUNPOS_HOME' "
						+ "or the system property 'hunpos.home' point to the Hunpos "
						+ "installation directory.");
	}

	private static List<String> getSearchPaths() {
		return getSearchPaths(Collections.<String> emptyList());
	}

	private static List<String> getSearchPaths(List<String> additionalPaths) {
		List<String> paths = new ArrayList<String>();
		paths.addAll(additionalPaths);
		if (System.getProperty("hunpos.home") != null) {
			paths.add(System.getProperty("hunpos.home"));
		}
		if (System.getenv("HUNPOS_HOME") != null) {
			paths.add(System.getenv("HUNPOS_HOME"));
		}
		return paths;
	}
}
