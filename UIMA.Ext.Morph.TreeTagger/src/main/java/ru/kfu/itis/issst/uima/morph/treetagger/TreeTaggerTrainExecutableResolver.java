/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import java.util.Collections;
import java.util.List;

import org.annolab.tt4j.Util;

import ru.kfu.itis.issst.uima.morph.commons.ExecutableResolverBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TreeTaggerTrainExecutableResolver extends ExecutableResolverBase {

	public static final String TRAINER_BASENAME = "train-tree-tagger";

	public TreeTaggerTrainExecutableResolver() {
		super(TRAINER_BASENAME);
	}

	@Override
	protected List<String> getSearchPaths() {
		return Util.getSearchPaths(Collections.<String> emptyList(), "bin");
	}

	@Override
	protected String hintOnNotFound() {
		return "Make sure the environment variable 'TREETAGGER_HOME' or "
				+ "'TAGDIR' or the system property 'treetagger.home' point to the TreeTagger "
				+ "installation directory.";
	}

}
