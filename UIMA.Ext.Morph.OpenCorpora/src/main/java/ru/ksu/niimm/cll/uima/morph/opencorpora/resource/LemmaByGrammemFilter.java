/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.BitSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LemmaByGrammemFilter implements LemmaFilter {

	private Set<String> grammemsToReject;

	public LemmaByGrammemFilter(String... grammemsToReject) {
		this.grammemsToReject = ImmutableSet.copyOf(grammemsToReject);
	}

	@Override
	public boolean accept(MorphDictionary dict, Lemma lemma) {
		BitSet grBits = lemma.getGrammems();
		for (int i = grBits.nextSetBit(0); i >= 0; i = grBits.nextSetBit(i + 1)) {
			Grammeme gr = dict.getGrammem(i);
			if (grammemsToReject.contains(gr.getId())) {
				return false;
			}
		}
		return true;
	}

}