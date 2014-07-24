/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static org.junit.Assert.assertEquals;
import static ru.kfu.itis.issst.uima.morph.commons.GramModelBasedTagMapper.parseTag;
import static ru.kfu.itis.issst.uima.morph.commons.GramModelLoader4Tests.gm;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class AgreementPredicateTest {

	@BeforeClass
	public static void loadGramModel() throws Exception {
		GramModelLoader4Tests.init();
	}

	@Test
	public void testCaseAgreement() {
		AgreementPredicate pred = AgreementPredicates.caseAgreement(gm);
		assertEquals(false, pred.apply(bits("NOUN"), bits("NOUN&gent")));
		assertEquals(true, pred.apply(bits("NOUN&gent"), bits("ADJF&gent")));
		assertEquals(true, pred.apply(bits("NOUN&gen1"), bits("ADJF&gent")));
		assertEquals(true, pred.apply(bits("NOUN&gen1"), bits("ADJF&gen1")));
		assertEquals(true, pred.apply(bits("NOUN&gent"), bits("NOUN&gen2")));
		assertEquals(true, pred.apply(bits("NOUN&gen2"), bits("NOUN&gen2")));
		assertEquals(false, pred.apply(bits("NOUN&gen1"), bits("NOUN&gen2")));
		assertEquals(true, pred.apply(bits("ADJF&nomn"), bits("ADJF&acc2")));
		assertEquals(false, pred.apply(bits("ADJF&loct"), bits("ADJF&ablt")));
		assertEquals(false, pred.apply(bits(""), bits("ADJF&ablt")));
		assertEquals(false, pred.apply(bits("CONJ"), bits("VERB")));
		assertEquals(true, pred.apply(bits("NOUN&accs&sing"), bits("ADJF&accs&plur")));
		assertEquals(true, pred.apply(bits("NOUN&nomn&sing"), bits("ADJF&nomn&plur")));
		assertEquals(false, pred.apply(bits("NOUN&nomn&sing"), bits("ADJF&datv&plur")));
		assertEquals(true, pred.apply(bits("NOUN&datv&sing"), bits("ADJF&datv&plur")));
		assertEquals(true, pred.apply(bits("NOUN&ablt&sing"), bits("ADJF&ablt&plur")));
		assertEquals(true, pred.apply(bits("NOUN&loct&sing"), bits("ADJF&loct&plur")));
		assertEquals(true, pred.apply(bits("NOUN&loct&sing"), bits("ADJF&loc1&plur")));
		assertEquals(true, pred.apply(bits("NOUN&loc2&sing"), bits("ADJF&loct&plur")));
		assertEquals(false, pred.apply(bits("NOUN&loc2&sing"), bits("ADJF&loc1&plur")));
	}

	@Test
	public void testNumberAggreement() {
		AgreementPredicate pred = AgreementPredicates.numberAgreement(gm);
		assertEquals(false, pred.apply(bits("CONJ"), bits("NOUN&sing")));
		assertEquals(false, pred.apply(bits("ADJF&plur"), bits("")));
		assertEquals(false, pred.apply(bits("ADJF&plur"), bits("NOUN&sing")));
		assertEquals(false, pred.apply(bits("ADJF&sing"), bits("NPRO&plur")));
		assertEquals(true, pred.apply(bits("ADJF&sing"), bits("NOUN&sing")));
		assertEquals(true, pred.apply(bits("ADJF&plur&Apro"), bits("NOUN&plur")));
	}

	@Test
	public void testGenderAgreement() {
		AgreementPredicate pred = AgreementPredicates.genderAgreement(gm);
		assertEquals(false, pred.apply(bits(""), bits("")));
		assertEquals(false, pred.apply(bits("VERB"), bits("")));
		assertEquals(false, pred.apply(bits("ADJF&GNdr&Apro"), bits("NUMR")));
		assertEquals(true, pred.apply(bits("ADJF&plur&masc&Apro"), bits("NOUN&sing&GNdr")));
		assertEquals(true, pred.apply(bits("NOUN&sing&GNdr"), bits("ADJF&plur&neut&Apro")));
		assertEquals(true, pred.apply(bits("ADJF&plur&femn&Apro"), bits("NOUN&sing&GNdr")));
		assertEquals(true, pred.apply(bits("NOUN&plur&GNdr"), bits("NOUN&sing&GNdr")));
		assertEquals(true, pred.apply(bits("NOUN&plur&masc"), bits("ADJF&sing&masc")));
		assertEquals(true, pred.apply(bits("NOUN&plur&neut"), bits("ADJF&sing&neut")));
		assertEquals(true, pred.apply(bits("NOUN&plur&femn"), bits("ADJF&sing&femn")));
		assertEquals(false, pred.apply(bits("NOUN&plur&femn"), bits("ADJF&sing&neut")));
		assertEquals(false, pred.apply(bits("NOUN&plur&femn"), bits("ADJF&sing&masc")));
		assertEquals(false, pred.apply(bits("NOUN&plur&masc"), bits("ADJF&sing&neut")));
	}

	private BitSet bits(String tag) {
		return toGramBits(gm, parseTag(tag));
	}
}
