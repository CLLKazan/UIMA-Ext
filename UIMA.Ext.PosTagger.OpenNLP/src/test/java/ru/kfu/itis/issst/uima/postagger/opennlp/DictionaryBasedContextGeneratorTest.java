/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ru.kfu.itis.issst.uima.morph.commons.DictionaryLoader4Tests.dict;

import java.util.Arrays;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;

import static org.hamcrest.CoreMatchers.*;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import com.google.common.collect.ImmutableList;

import ru.kfu.cll.uima.tokenizer.fstype.CW;
import ru.kfu.cll.uima.tokenizer.fstype.PM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.morph.commons.DictionaryLoader4Tests;
import ru.kfu.itis.issst.uima.postagger.PosTaggerAPI;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryBasedContextGeneratorTest {

	@BeforeClass
	public static void loadDictionary() {
		DictionaryLoader4Tests.init();
	}

	final DictionaryBasedContextGenerator gen;

	// this text has been made ugly intentionally to simplify the test implementation
	final String testTxt = "Tales of Legendia — компьютерный игРа в жанре мозявки";

	final JCas jCas;

	public DictionaryBasedContextGeneratorTest() throws ResourceInitializationException,
			CASException {
		gen = new DictionaryBasedContextGenerator(Arrays.asList("POST", "NMbr", "GNdr", "CAse"),
				dict);
		TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
				TokenizerAPI.TYPESYSTEM_TOKENIZER,
				PosTaggerAPI.TYPESYSTEM_POSTAGGER);
		CAS cas = CasCreationUtils.createCas(tsd, null, null);
		cas.setDocumentText(testTxt);
		jCas = cas.getJCas();
	}

	@Test
	public void testOnPunctuation() {
		Token token = new PM(jCas, 18, 19);
		assertEquals(ImmutableList.<String> of(), gen.extract(token, null));
	}

	@Test
	public void testOnNonRussian() {
		Token token = new CW(jCas, 9, 17);
		assertEquals(ImmutableList.<String> of("DL=NotRussian"), gen.extract(token, null));
	}

	@Test
	public void testOnNonDictionary() {
		Token token = new CW(jCas, 46, 53);
		assertEquals(ImmutableList.<String> of("DL=Unknown"), gen.extract(token, null));
	}

	@Test
	public void testOnKnownWordWithoutPrevTag() {
		// игра
		Token token = new CW(jCas, 33, 37);
		Assert.assertThat("Expected at least one dictionary entry",
				gen.extract(token, null),
				hasItem("DL=NOUN_femn_sing_nomn"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testOnKnownWordWithPrevTag() {
		// компьютерный
		Token token = new CW(jCas, 20, 32);
		// force cast to avoid compilation error
		assertThat("Expected at least two dictionary entries and agreement",
				gen.extract(token, "ADJF&femn&sing&accs"),
				(Matcher) hasItems(
						allOf(startsWith("DL="), containsString("accs")),
						allOf(startsWith("DL="), containsString("nomn")),
						is("CaseAgr"), is("NumberAgr"), is("NumberCaseAgr")));
		//
		assertThat("Expected at least two dictionary entries and agreement",
				gen.extract(token, "ADJF&masc&plur&nomn"),
				(Matcher) hasItems(is("CaseAgr"), is("GenderAgr"), is("GenderCaseAgr")));
	}
}
