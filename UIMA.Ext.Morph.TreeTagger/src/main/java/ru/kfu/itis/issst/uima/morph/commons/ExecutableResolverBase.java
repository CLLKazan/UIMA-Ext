/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.annolab.tt4j.ExecutableResolver;
import org.annolab.tt4j.PlatformDetector;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class ExecutableResolverBase implements ExecutableResolver {

	protected PlatformDetector platformDetector;
	protected String executableBaseName;

	protected ExecutableResolverBase(String executableBaseName) {
		this.executableBaseName = executableBaseName;
	}

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

			File exe = new File(p + separator + executableBaseName
					+ platformDetector.getExecutableSuffix());
			searchedIn.add(exe.getAbsolutePath());
			if (exe.exists()) {
				return exe.getAbsolutePath();
			}
		}
		throw new IOException(String.format(
				"Unable to locate executable binary '%s' in the following locations %s.\n%s",
				executableBaseName, searchedIn, hintOnNotFound()));
	}

	protected abstract List<String> getSearchPaths();

	protected abstract String hintOnNotFound();
}
