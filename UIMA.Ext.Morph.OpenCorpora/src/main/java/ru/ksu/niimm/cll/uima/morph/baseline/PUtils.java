/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.baseline;

import java.util.BitSet;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.opencorpora.cas.Word;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils;

/**
 * package-private utils
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class PUtils {

	public static BitSet toGramBitSet(MorphDictionary dict, org.opencorpora.cas.Wordform casWf) {
		return MorphDictionaryUtils.toGramBits(dict, FSUtils.toList(casWf.getGrammems()));
	}

	public static boolean canCarryWord(Token token) {
		return token instanceof W || token instanceof NUM;
	}

	public static org.opencorpora.cas.Wordform addCasWordform(JCas jCas, Annotation tokenAnno) {
		Word word = new Word(jCas);
		word.setBegin(tokenAnno.getBegin());
		word.setEnd(tokenAnno.getEnd());
		word.setToken(tokenAnno);
		org.opencorpora.cas.Wordform casWf = new org.opencorpora.cas.Wordform(jCas);
		casWf.setWord(word);
		word.setWordforms(FSUtils.toFSArray(jCas, casWf));
		//
		word.addToIndexes();
		//
		return casWf;
	}

	public static String normalizeToDictionary(String tokenStr) {
		return WordUtils.normalizeToDictionaryForm(tokenStr);
	}

	private PUtils() {
	}
}