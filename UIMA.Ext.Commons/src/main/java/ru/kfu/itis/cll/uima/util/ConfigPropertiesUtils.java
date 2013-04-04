/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

/**
 * @author Rinat Gareev
 * 
 */
public class ConfigPropertiesUtils {

	public static Properties replacePlaceholders(Properties props,
			Map<String, String> placeholderValues) {
		return replacePlaceholders(props, placeholderValues, false);
	}

	/**
	 * Replace ${}-placeholders inside properties values.
	 * 
	 * @param props
	 *            target {@link Properties} instance
	 * @param placeholderValues
	 * @param ignoreAbsent
	 *            if false then absent values for some placeholder key will
	 *            raise {@link IllegalStateException}
	 * @return given {@link Properties} instance
	 */
	public static Properties replacePlaceholders(Properties props,
			Map<String, String> placeholderValues, boolean ignoreAbsent) {
		Map<String, String> replacements = Maps.newHashMap();
		for (String propKey : props.stringPropertyNames()) {
			String propValue = props.getProperty(propKey);
			Matcher phMatcher = PLACEHOLDER_PATTERN.matcher(propValue);
			StringBuffer sb = new StringBuffer(propValue.length());
			while (phMatcher.find()) {
				String replacement = placeholderValues.get(phMatcher.group(1));
				if (replacement == null) {
					if (ignoreAbsent) {
						replacement = phMatcher.group();
					} else {
						throw new IllegalArgumentException(String.format(
								"Can't find value for placeholder %s", phMatcher.group()));
					}
				}
				phMatcher.appendReplacement(sb, replacement);
			}
			phMatcher.appendTail(sb);
			String resultValue = sb.toString();
			if (!resultValue.equals(propValue)) {
				replacements.put(propKey, resultValue);
			}
		}
		for (String propKey : replacements.keySet()) {
			props.setProperty(propKey, replacements.get(propKey));
		}
		return props;
	}

	private static final Pattern PLACEHOLDER_PATTERN = Pattern
			.compile("\\$\\{([\\p{Alnum}._]+)\\}");

	private ConfigPropertiesUtils() {
	}

}