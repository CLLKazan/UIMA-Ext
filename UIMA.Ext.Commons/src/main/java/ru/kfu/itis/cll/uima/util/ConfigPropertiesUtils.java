/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.kfu.itis.cll.uima.io.IoUtils;

import com.google.common.collect.Maps;

/**
 * @see IoUtils
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
				phMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
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

	public static String prettyString(Properties props) {
		StringBuilder sb = new StringBuilder();
		for (String key : props.stringPropertyNames()) {
			sb.append(key).append("=");
			sb.append(props.getProperty(key));
			sb.append("\n");
		}
		return sb.toString();
	}

	public static Integer getIntProperty(Properties props, String key, boolean required) {
		String valStr = props.getProperty(key);
		if (valStr == null) {
			if (required)
				throw new IllegalStateException(String.format("No value for '%s'", key));
			else
				return null;
		}
		try {
			return Integer.valueOf(valStr);
		} catch (NumberFormatException e) {
			throw new IllegalStateException(String.format("Can't parse %s='%s'",
					key, valStr));
		}
	}

	public static Integer getIntProperty(Properties props, String key) {
		return getIntProperty(props, key, true);
	}

	private ConfigPropertiesUtils() {
	}

}