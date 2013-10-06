/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.opencorpora.cas.Word;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * <p>
 * Uses Wordform.pos to set general lexical category, e.g., NOUN,VERB, etc.
 * <p>
 * Uses Wordform.grammems to set all grammatical categories, including general
 * one.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DefaultAnnotationAdapter extends AnnotationAdapterBase {

	@Override
	public void apply(JCas jcas, Annotation token, Collection<Wordform> dictWfs) {
		Word word = new Word(jcas);
		word.setBegin(token.getBegin());
		word.setEnd(token.getEnd());
		word.setToken(token);
		List<org.opencorpora.cas.Wordform> casWfList = Lists.newLinkedList();
		for (Wordform wf : dictWfs) {
			org.opencorpora.cas.Wordform casWf = new org.opencorpora.cas.Wordform(jcas);

			BitSet grammems = wf.getGrammems();
			Lemma lemma = dict.getLemma(wf.getLemmaId());
			// set lemma id
			casWf.setLemmaId(lemma.getId());
			// set lemma norm
			casWf.setLemma(lemma.getString());
			// set pos
			casWf.setPos(dict.getPos(lemma));
			// set grammems
			grammems.or(lemma.getGrammems());
			List<String> gramSet = dict.toGramSet(grammems);
			casWf.setGrammems(FSUtils.toStringArray(jcas, gramSet));

			// set hosting word
			casWf.setWord(word);

			casWfList.add(casWf);
		}
		// set wordforms
		word.setWordforms(FSUtils.toFSArray(jcas, casWfList));

		word.addToIndexes();
	}

	@Override
	public void apply(JCas jcas, Annotation token,
			Integer lexemeId, final String _lemma, BitSet posBits) {
		Word word = new Word(jcas);
		word.setBegin(token.getBegin());
		word.setEnd(token.getEnd());
		word.setToken(token);

		org.opencorpora.cas.Wordform casWf = new org.opencorpora.cas.Wordform(jcas);
		String lemma = null;
		if (lexemeId != null) {
			Lemma lex = dict.getLemma(lexemeId);
			lemma = lex.getString();
			casWf.setLemmaId(lexemeId);
		} else if (_lemma != null) {
			lemma = _lemma;
		}
		if (lemma != null) {
			casWf.setLemma(lemma);
		}
		// TODO set 'pos' feature
		// casWf.setPos(...);

		List<String> gramSet = dict.toGramSet(posBits);
		casWf.setGrammems(FSUtils.toStringArray(jcas, gramSet));

		// set hosting word
		casWf.setWord(word);

		// set wordforms
		word.setWordforms(FSUtils.toFSArray(jcas, ImmutableList.of(casWf)));

		word.addToIndexes();
	}
}