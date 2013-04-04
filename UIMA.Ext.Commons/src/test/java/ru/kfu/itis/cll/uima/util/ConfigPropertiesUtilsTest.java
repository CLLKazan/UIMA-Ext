/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev
 * 
 */
public class ConfigPropertiesUtilsTest {

	@Test
	public void test1() {
		Properties props = new Properties();
		props.setProperty("year", "2013");
		props.setProperty("outputDir", "./${baseDir}/output");
		props.setProperty("inputDir", "${baseDir}/input");
		props.setProperty("configDir", "${baseDir}");

		Map<String, String> phValues = Maps.newHashMap();
		phValues.put("baseDir", "current");
		ConfigPropertiesUtils.replacePlaceholders(props, phValues, false);
		assertEquals("2013", props.getProperty("year"));
		assertEquals("./current/output", props.getProperty("outputDir"));
		assertEquals("current/input", props.getProperty("inputDir"));
		assertEquals("current", props.getProperty("configDir"));
	}

}