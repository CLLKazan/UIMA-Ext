/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import java.util.ArrayList;
import java.util.List;

import org.annolab.tt4j.ExecutableResolver;

import ru.kfu.itis.issst.uima.morph.commons.ExecutableResolverBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultHunposExecutableResolver extends ExecutableResolverBase {

	public static final String HUNPOS_TAGGER_FILE_BASENAME = "hunpos-tag";
	public static final String HUNPOS_TRAINER_FILE_BASENAME = "hunpos-train";

	public static ExecutableResolver taggerResolver() {
		return new DefaultHunposExecutableResolver(HUNPOS_TAGGER_FILE_BASENAME);
	}

	public static ExecutableResolver trainerResolver() {
		return new DefaultHunposExecutableResolver(HUNPOS_TRAINER_FILE_BASENAME);
	}

	private DefaultHunposExecutableResolver(String executableBaseName) {
		super(executableBaseName);
	}

	@Override
	protected List<String> getSearchPaths() {
		return getHunposSearchPaths();
	}

	@Override
	protected String hintOnNotFound() {
		return "Make sure the environment variable 'HUNPOS_HOME' "
				+ "or the system property 'hunpos.home' point to the Hunpos "
				+ "installation directory.";
	}

	static List<String> getHunposSearchPaths() {
		List<String> paths = new ArrayList<String>();
		if (System.getProperty("hunpos.home") != null) {
			paths.add(System.getProperty("hunpos.home"));
		}
		if (System.getenv("HUNPOS_HOME") != null) {
			paths.add(System.getenv("HUNPOS_HOME"));
		}
		return paths;
	}
}
