/**
 * 
 */
package ru.kfu.itis.issst.cleartk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarInputStream;

import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.cleartk.ml.jar.JarClassifierBuilder;
import org.apache.uima.fit.component.initialize.ConfigurationParameterInitializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.initializable.Initializable;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * This is re-implementation of ClearTK-ML
 * {@link org.cleartk.ml.jar.GenericJarClassifierFactory} that resolve
 * 'classifierJarPath' using an UIMA default {@link ResourceManager} with
 * provided 'additionalSearchPaths'.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class GenericJarClassifierFactory<CLASSIFIER_TYPE> implements Initializable {

	public static final String PARAM_CLASSIFIER_JAR_PATH = "classifierJarPath";
	public static final String PARAM_ADDITIONAL_SEARCH_PATHS = "additionalSearchPaths";

	@ConfigurationParameter(name = PARAM_ADDITIONAL_SEARCH_PATHS, mandatory = false)
	private String[] additionalSearchPaths;

	@ConfigurationParameter(
			name = PARAM_CLASSIFIER_JAR_PATH,
			mandatory = true,
			description = "provides the path to the jar file that should be used to instantiate the classifier.")
	private String classifierJarPath;

	public void setClassifierJarPath(String classifierJarPath) {
		this.classifierJarPath = classifierJarPath;
	}

	public void initialize(UimaContext context) throws ResourceInitializationException {
		ConfigurationParameterInitializer.initialize(this, context);
	}

	public CLASSIFIER_TYPE createClassifier() throws IOException {
		ResourceManager rm = UIMAFramework.newDefaultResourceManager();
		if (additionalSearchPaths != null) {
			List<String> dpElements = Lists.newLinkedList();
			dpElements.add(rm.getDataPath());
			dpElements.addAll(Arrays.asList(additionalSearchPaths));
			rm.setDataPath(dataPathJoiner.join(dpElements));
		}
		URL classifierJarURL = rm.resolveRelativePath(classifierJarPath);
		if (classifierJarURL == null) {
			throw new IllegalStateException(String.format(
					"Can't resolve path '%s' using an UIMA default resource manager "
							+ "and these additional paths: %s",
					classifierJarPath, Arrays.toString(additionalSearchPaths)));
		}
		InputStream stream = classifierJarURL.openStream();
		try {
			stream = new BufferedInputStream(stream);
			JarInputStream modelStream = new JarInputStream(stream);
			JarClassifierBuilder<?> builder = JarClassifierBuilder.fromManifest(modelStream
					.getManifest());

			return this.getClassifierClass().cast(builder.loadClassifier(modelStream));
		} finally {
			stream.close();
		}
	}

	private static final Joiner dataPathJoiner = Joiner.on(File.pathSeparatorChar).skipNulls();

	protected abstract Class<CLASSIFIER_TYPE> getClassifierClass();

}
