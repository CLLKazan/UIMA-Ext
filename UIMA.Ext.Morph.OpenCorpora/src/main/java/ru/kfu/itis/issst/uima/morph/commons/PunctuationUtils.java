/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PunctuationUtils {

	public static final Map<String, String> punctuationTagMap;
	// tag for unknown punctuation marks or special symbols 
	public static final String OTHER_PUNCTUATION_TAG = "_P_";

	static {
		ImmutableMap.Builder<String, String> b = ImmutableMap.builder();
		// dashes
		b.put("\u2012", "--");
		b.put("\u2013", "--");
		b.put("\u2014", "--");
		b.put("\u2015", "--");
		// hyphens
		b.put("-", "-");
		b.put("\u2010", "-");
		b.put("\u00AD", "-");
		b.put("\u2011", "-");
		b.put("\u2043", "-");
		// apostrophe
		b.put("'", "'");
		b.put("\u2018", "'");
		b.put("\u2019", "'");
		// brackets
		b.put("(", "(");
		b.put(")", ")");
		b.put("[", "(");
		b.put("]", ")");
		b.put("{", "(");
		b.put("}", ")");
		// colon
		b.put(":", ":");
		// semicolon
		b.put(";", ";");
		// comma
		b.put(",", ",");
		// exclamation
		b.put("!", "!");
		b.put("\u203C", "!");
		// period
		b.put(".", ".");
		// question mark
		b.put("?", "?");
		// quotation marks
		b.put("\"", "\"");
		b.put("\u00AB", "\"");
		b.put("\u2039", "\"");
		b.put("\u00BB", "\"");
		b.put("\u203A", "\"");
		b.put("\u201A", "\"");
		b.put("\u201B", "\"");
		b.put("\u201C", "\"");
		b.put("\u201D", "\"");
		b.put("\u201E", "\"");
		b.put("\u201F", "\"");
		// slashes
		b.put("\\", "\\");
		b.put("/", "/");
		// well, these are not punctuation marks
		// but for simplicity we will put them in the same map
		b.put("$", "$");
		b.put("%", "%");
		punctuationTagMap = b.build();
	}

	public static String getPunctuationTag(String tokenStr) {
		String tag = punctuationTagMap.get(tokenStr);
		if (tag == null) {
			tag = OTHER_PUNCTUATION_TAG;
		}
		return tag;
	}

	private PunctuationUtils() {
	}
}
