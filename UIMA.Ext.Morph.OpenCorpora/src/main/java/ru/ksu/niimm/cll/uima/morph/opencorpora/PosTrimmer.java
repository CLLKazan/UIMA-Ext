/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.opencorpora.cas.Wordform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTrimmer {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private GramModel gramModel;
	private Set<String> targetPosCategories;
	// derived
	private final BitSet targetBits; // DO NOT MODIFY!
	private Set<String> targetTags;

	public PosTrimmer(GramModel gramModel, String... targetPosCategories) {
		this(gramModel, ImmutableSet.copyOf(targetPosCategories));
	}

	public PosTrimmer(GramModel _gramModel, Set<String> _targetPosCategories) {
		this.gramModel = _gramModel;
		targetPosCategories = ImmutableSet.copyOf(_targetPosCategories);
		//
		targetBits = new BitSet();
		for (String cat : targetPosCategories) {
			BitSet catBS = gramModel.getGrammemWithChildrenBits(cat, true);
			if (catBS == null) {
				throw new IllegalStateException(String.format("Unknown grammeme %s", cat));
			}
			targetBits.or(catBS);
		}
		// 
		targetTags = ImmutableSet.copyOf(gramModel.toGramSet(targetBits));
		log.info("PosTrimmer will retain following gram tags:\n{}", targetTags);
	}

	public void trim(JCas jCas, Wordform wf) {
		StringArray grammemsFS = wf.getGrammems();
		Set<String> grammems = Sets.newLinkedHashSet(FSUtils.toSet(grammemsFS));
		if (grammems.retainAll(targetTags)) {
			wf.setGrammems(FSUtils.toStringArray(jCas, grammems));
		}
	}

	public void trimInPlace(Collection<String> grammems) {
		grammems.retainAll(targetTags);
	}

	public void trimInPlace(BitSet posBits) {
		posBits.and(targetBits);
	}

	public Set<BitSet> trimAndMerge(Iterable<BitSet> srcCol) {
		Set<BitSet> result = Sets.newHashSet();
		for (final BitSet _posBits : srcCol) {
			BitSet posBits = (BitSet) _posBits.clone();
			trimInPlace(posBits);
			result.add(posBits);
		}
		return result;
	}
}