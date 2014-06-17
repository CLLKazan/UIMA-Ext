/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AgreementPredicate implements TwoTagPredicate {

	public static AgreementPredicate caseAgreement(GramModel gm) {
		return caseAgrBuilder.build(gm);
	}

	public static AgreementPredicate numberAgreement(GramModel gm) {
		return numberAgrBuilder.build(gm);
	}

	public static AgreementPredicate genderAgreement(GramModel gm) {
		return genderAgrBuilder.build(gm);
	}

	private static final Builder caseAgrBuilder = new Builder()
			.agree(nomn)
			.agree(gent).agree(gent, gen1).agree(gent, gen2).agree(gen1).agree(gen2)
			.agree(accs).agree(nomn, acc2).agree(acc2)
			.agree(datv)
			.agree(ablt)
			.agree(loct).agree(loct, loc1).agree(loct, loc2).agree(loc1).agree(loc2)
			.agree(voct);

	private static final Builder numberAgrBuilder = new Builder()
			.agree(sing)
			.agree(plur);

	private static final Builder genderAgrBuilder = new Builder()
			.agree(masc).agree(masc, GNdr)
			.agree(femn).agree(femn, GNdr)
			.agree(neut).agree(neut, GNdr)
			.agree(GNdr);

	public static class Builder {
		private Multimap<Set<String>, Set<String>> agreementMap = HashMultimap.create();

		public Builder agree(String gram1) {
			return agree(gram1, gram1);
		}

		public Builder agree(String gram1, String gram2) {
			return agree(ImmutableSet.of(gram1), ImmutableSet.of(gram2));
		}

		public Builder agree(String gram1, Set<String> gram2) {
			return agree(ImmutableSet.of(gram1), gram2);
		}

		public Builder agree(Set<String> gram1, Set<String> gram2) {
			gram1 = ImmutableSet.copyOf(gram1);
			gram2 = ImmutableSet.copyOf(gram2);
			agreementMap.put(gram1, gram2);
			if (!gram1.equals(gram2)) {
				// add symmetrical
				agreementMap.put(gram2, gram1);
			}
			return this;
		}

		// MUST NOT change the state of this builder 
		public AgreementPredicate build(GramModel gm) {
			// convert Strings to BitSet
			Multimap<BitSet, BitSet> bitAgrMap = HashMultimap.create();
			for (Set<String> key : agreementMap.keySet()) {
				BitSet keyBits = toGramBits(gm, key);
				for (Set<String> value : agreementMap.get(key)) {
					BitSet valueBits = toGramBits(gm, value);
					bitAgrMap.put(keyBits, valueBits);
				}
			}
			return new AgreementPredicate(gm, bitAgrMap);
		}
	}

	private final GramModel gramModel;
	private final BitSet categoryMask;
	private final Multimap<BitSet, BitSet> agreementMap;

	private static final Logger log = LoggerFactory.getLogger(AgreementPredicate.class);

	private AgreementPredicate(GramModel gm, Multimap<BitSet, BitSet> agreementMap) {
		this.gramModel = gm;
		this.agreementMap = ImmutableMultimap.copyOf(agreementMap);
		categoryMask = new BitSet();
		for (Map.Entry<BitSet, BitSet> e : agreementMap.entries()) {
			categoryMask.or(e.getKey());
			categoryMask.or(e.getValue());
		}
		// done
	}

	@Override
	public boolean apply(BitSet _first, BitSet _second) {
		BitSet first = (BitSet) _first.clone();
		BitSet second = (BitSet) _second.clone();
		// 
		first.and(categoryMask);
		if (first.isEmpty()) {
			return false;
		}
		second.and(categoryMask);
		if (second.isEmpty()) {
			return false;
		}
		//
		Collection<BitSet> equivs = agreementMap.get(first);
		if (equivs.isEmpty()) {
			log.warn("Unknown combination of gramemmes: {}", gramModel.toGramSet(first));
		}
		for (BitSet aggr : equivs) {
			if (aggr.equals(second)) {
				return true;
			}
		}
		return false;
	}
}
