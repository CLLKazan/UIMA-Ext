/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import opennlp.tools.postag.POSContextGenerator;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ACPOSContextGenerator implements POSContextGenerator {

	private final int prevTagsInHistory;

	public ACPOSContextGenerator() {
		this(2);
	}

	public ACPOSContextGenerator(int prevTagsInHistory) {
		this.prevTagsInHistory = prevTagsInHistory;
	}

	@Override
	public String[] getContext(int pos, String[] tokens, String[] prevTags, Object[] ac) {
		ArrayList<String> result = Lists.newArrayListWithExpectedSize(
				ac == null ? 0 : ac.length + 2);
		for (Object acVal : ac) {
			if (acVal instanceof String) {
				result.add((String) acVal);
			}
		}
		if (prevTags == null) {
			prevTags = EMPTY_STRING_ARRAY;
		}
		for (int pt = 1; pt <= prevTagsInHistory; pt++) {
			int t = prevTags.length - pt;
			if (t < 0) {
				break;
			}
			String val = new StringBuilder("pt").append(pt).append('=').append(prevTags[t])
					.toString();
			result.add(val);
		}
		return result.toArray(new String[result.size()]);
	}

	private static final String[] EMPTY_STRING_ARRAY = new String[0];
}
