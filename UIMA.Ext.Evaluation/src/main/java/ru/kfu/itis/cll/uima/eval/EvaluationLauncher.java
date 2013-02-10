/**
 * 
 */
package ru.kfu.itis.cll.uima.eval;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static ru.kfu.itis.cll.uima.eval.ConfigurationKeys.PREFIX_LISTENER_ID;
import static ru.kfu.itis.cll.uima.eval.ConfigurationKeys.PREFIX_LISTENER_PROPERTY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;

import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class EvaluationLauncher {

	private static final String APP_CONTEXT_LOCATION =
			"classpath:ru/kfu/itis/cll/uima/eval/app-context.xml";

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println(
					"Usage: <properties-config-filepath>");
			return;
		}
		File propsFile = new File(args[0]);
		if (!propsFile.isFile()) {
			System.err.println("Can't find file " + propsFile);
			return;
		}
		Properties configProperties = readProperties(propsFile);

		GenericApplicationContext appCtx = new GenericApplicationContext();

		appCtx.getEnvironment().getPropertySources().addLast(
				new PropertiesPropertySource("configFile", configProperties));

		XmlBeanDefinitionReader xmlBDReader = new XmlBeanDefinitionReader(appCtx);
		xmlBDReader.loadBeanDefinitions(APP_CONTEXT_LOCATION);

		// register listeners
		Map<String, String> listenerImpls = getPrefixedKeyPairs(configProperties,
				PREFIX_LISTENER_ID);
		for (String listenerId : listenerImpls.keySet()) {
			String listenerClass = listenerImpls.get(listenerId);
			BeanDefinitionBuilder bb = genericBeanDefinition(listenerClass);
			Map<String, String> listenerProperties = getPrefixedKeyPairs(configProperties,
					PREFIX_LISTENER_PROPERTY + listenerId + ".");
			for (String propName : listenerProperties.keySet()) {
				bb.addPropertyValue(propName, listenerProperties.get(propName));
			}
			appCtx.registerBeanDefinition(listenerId, bb.getBeanDefinition());
		}

		appCtx.refresh();

		appCtx.registerShutdownHook();

		GoldStandardBasedEvaluation eval = appCtx.getBean(GoldStandardBasedEvaluation.class);
		eval.run();
	}

	private static Properties readProperties(File srcFile) throws IOException {
		Properties result = new Properties();
		InputStream srcIS = new FileInputStream(srcFile);
		Reader srcReader = new BufferedReader(new InputStreamReader(srcIS, "utf-8"));
		try {
			result.load(srcReader);
		} finally {
			IOUtils.closeQuietly(srcReader);
		}
		return result;
	}

	private static Map<String, String> getPrefixedKeyPairs(Properties props, String prefix) {
		Map<String, String> result = Maps.newHashMap();
		for (String key : props.stringPropertyNames()) {
			if (key.startsWith(prefix)) {
				result.put(key.substring(prefix.length()), props.getProperty(key));
			}
		}
		return result;
	}

	private EvaluationLauncher() {
	}
}