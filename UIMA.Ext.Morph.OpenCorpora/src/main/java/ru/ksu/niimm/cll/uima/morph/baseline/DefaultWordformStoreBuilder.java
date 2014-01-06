/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.util.BitSet;
import java.util.Map;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class DefaultWordformStoreBuilder implements WordformStoreBuilder {

	// state
	private Map<String, Multiset<BitSet>> strKeyMap = Maps.newHashMap();

	@Override
	public void increment(String wordString, BitSet posBits) {
		Multiset<BitSet> posSet = strKeyMap.get(wordString);
		if (posSet == null) {
			posSet = HashMultiset.create();
			strKeyMap.put(wordString, posSet);
		}
		posSet.add(posBits);
	}

	@Override
	public DefaultWordformStore build() {
		DefaultWordformStore result = new DefaultWordformStore();
		result.strKeyMap = Maps.newHashMapWithExpectedSize(strKeyMap.size());
		for (String str : strKeyMap.keySet()) {
			Multiset<BitSet> gbsBag = strKeyMap.get(str);
			int max = 0;
			BitSet maxBS = null;
			for (Multiset.Entry<BitSet> gbsEntry : gbsBag.entrySet()) {
				if (gbsEntry.getCount() > max) {
					max = gbsEntry.getCount();
					maxBS = gbsEntry.getElement();
				}
			}
			if (maxBS == null) {
				throw new IllegalStateException();
			}
			result.strKeyMap.put(str, maxBS);
		}
		return result;
	}
}