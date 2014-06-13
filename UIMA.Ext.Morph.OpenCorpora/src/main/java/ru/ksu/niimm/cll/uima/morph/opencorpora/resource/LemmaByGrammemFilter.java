/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import java.util.BitSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LemmaByGrammemFilter implements LemmaPostProcessor {

	private Set<String> grammemsToReject;

	public LemmaByGrammemFilter(String... grammemsToReject) {
		this.grammemsToReject = ImmutableSet.copyOf(grammemsToReject);
	}

	@Override
	public boolean process(MorphDictionary dict, Lemma.Builder lemmaBuilder,
			Multimap<String, Wordform> wfMap) {
		BitSet grBits = lemmaBuilder.getGrammems();
		for (int i = grBits.nextSetBit(0); i >= 0; i = grBits.nextSetBit(i + 1)) {
			Grammeme gr = dict.getGramModel().getGrammem(i);
			if (grammemsToReject.contains(gr.getId())) {
				return false;
			}
		}
		return true;
	}

}