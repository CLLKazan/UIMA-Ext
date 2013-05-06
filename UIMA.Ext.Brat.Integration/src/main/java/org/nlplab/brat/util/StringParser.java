/**
 * 
 */
package org.nlplab.brat.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to facilitate Brat annotation.conf and *.ann files parsing.
 * 
 * @author Rinat Gareev
 * 
 */
public class StringParser {
	private String srcString;
	private String currentString;

	public StringParser(String srcString) {
		this.srcString = srcString;
		currentString = srcString;
	}

	public String getCurrentString() {
		return currentString;
	}

	public void ensureBlank() {
		if (!StringUtils.isBlank(currentString)) {
			throw new IllegalStateException(String.format(
					"Illegal ending '%s' in:\n%s", currentString, srcString));
		}
	}

	public String consume1(Pattern pattern) {
		return consume(pattern)[0];
	}

	public String[] consume(Pattern pattern) {
		Matcher m = pattern.matcher(currentString);
		if (m.lookingAt()) {
			String[] result = new String[m.groupCount() + 1];
			result[0] = m.group();
			for (int i = 1; i <= m.groupCount(); i++) {
				result[i] = m.group(i);
			}
			currentString = currentString.substring(m.end());
			return result;
		} else {
			throw new IllegalStateException(String.format(
					"'%s' expected in the beginning of '%s'", pattern, currentString));
		}
	}

	public void skip(Pattern pattern) {
		Matcher m = pattern.matcher(currentString);
		if (m.lookingAt()) {
			currentString = currentString.substring(m.end());
		} else {
			throw new IllegalStateException(String.format(
					"'%s' expected in the beginning of '%s'", pattern, currentString));
		}
	}
}