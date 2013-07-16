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
import ru.ksu.niimm.cll.uima.morph.AnnotationAdapterBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;

import com.google.common.collect.Lists;

/**
 * <p>
 * Uses Wordform.pos to set general lexical category, e.g., NOUN,VERB, etc.
 * <p>
 * Uses Wordform.grammems to set other grammatical categories.
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
			grammems.andNot(dict.getPosBits());
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

}