/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import static org.apache.commons.io.FileUtils.openOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.BitSet;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class DefaultWordformStoreBuilder implements WordformStoreBuilder {

	private static Logger log = LoggerFactory.getLogger(DefaultWordformStoreBuilder.class);

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
	public void persist(File outFile) throws Exception {
		// build
		DefaultWordformStore ws = build();
		// serialize store object
		ObjectOutputStream modelOS = null;
		try {
			OutputStream os = new BufferedOutputStream(openOutputStream(outFile));
			modelOS = new ObjectOutputStream(os);
			modelOS.writeObject(ws);
		} finally {
			IOUtils.closeQuietly(modelOS);
		}
		log.info("WordformStore succesfully serialized to {}, size = {} bytes",
				outFile, outFile.length());
	}

	private DefaultWordformStore build() {
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