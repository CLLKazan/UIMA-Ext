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
		ContextGeneratorUtils.addPreviousTags(prevTagsInHistory, prevTags, result);
		return result.toArray(new String[result.size()]);
	}
}
