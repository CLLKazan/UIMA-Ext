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

	public static void addPreviousTags(int index, String[] prevTags, int prevTagsToAdd,
			Collection<String> targetCol) {
		if (prevTags == null) {
			prevTags = EMPTY_STRING_ARRAY;
		}
		// sanity check - prev tags must be defined at least till index-1
		if (index - 1 >= prevTags.length) {
			throw new IllegalStateException();
		}
		for (int pt = 1; pt <= prevTagsToAdd; pt++) {
			int t = index - pt;
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
