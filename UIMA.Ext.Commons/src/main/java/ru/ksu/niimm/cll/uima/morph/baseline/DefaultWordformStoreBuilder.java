/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class DefaultWordformStoreBuilder<TagType> implements WordformStoreBuilder<TagType> {

	// state
	private Map<String, Multiset<TagType>> strKeyMap = Maps.newHashMap();

	@Override
	public void increment(String wordString, TagType tag) {
		Multiset<TagType> tags = strKeyMap.get(wordString);
		if (tags == null) {
			tags = HashMultiset.create();
			strKeyMap.put(wordString, tags);
		}
		tags.add(tag);
	}

	@Override
	public DefaultWordformStore<TagType> build() {
		DefaultWordformStore<TagType> result = new DefaultWordformStore<TagType>();
		result.strKeyMap = Maps.newHashMapWithExpectedSize(strKeyMap.size());
		for (String wf : strKeyMap.keySet()) {
			Multiset<TagType> tagBag = strKeyMap.get(wf);
			int max = 0;
			TagType maxTag = null;
			for (Multiset.Entry<TagType> tagEntry : tagBag.entrySet()) {
				if (tagEntry.getCount() > max) {
					max = tagEntry.getCount();
					maxTag = tagEntry.getElement();
				}
			}
			if (maxTag == null) {
				throw new IllegalStateException();
			}
			result.strKeyMap.put(wf, maxTag);
		}
		return result;
	}
}