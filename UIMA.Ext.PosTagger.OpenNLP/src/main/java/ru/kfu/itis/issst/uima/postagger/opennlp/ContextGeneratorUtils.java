/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.Collection;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ContextGeneratorUtils {

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static void addPreviousTags(int prevTagsToAdd, String[] prevTags,
			Collection<String> targetCol) {
		if (prevTags == null) {
			prevTags = EMPTY_STRING_ARRAY;
		}
		for (int pt = 1; pt <= prevTagsToAdd; pt++) {
			int t = prevTags.length - pt;
			if (t < 0) {
				break;
			}
			String val = new StringBuilder("pt").append(pt)
					.append('=')
					.append(prevTags[t])
					.toString();
			targetCol.add(val);
		}
	}

	private ContextGeneratorUtils() {
	}
}
