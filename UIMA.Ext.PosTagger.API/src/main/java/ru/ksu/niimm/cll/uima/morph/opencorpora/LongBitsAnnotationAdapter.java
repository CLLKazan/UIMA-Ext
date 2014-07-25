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

import com.google.common.collect.Lists;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.issst.uima.morph.dictionary.AnnotationAdapterBase;
import ru.kfu.itis.issst.uima.morph.model.Lemma;
import ru.kfu.itis.issst.uima.morph.model.Wordform;

/**
 * Save gram tag bits into Wordform.posBits and (optionally) their string
 * representations into Wordform.pos
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class LongBitsAnnotationAdapter extends AnnotationAdapterBase {

	/**
	 * {@inheritDoc}
	 */
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
			casWf.setPos(dict.getGramModel().getPos(lemma.getGrammems()));
			// set grammems
			grammems.or(lemma.getGrammems());
			// XXX
			// XXX fill posBits long array
			// XXX
			// set hosting word
			casWf.setWord(word);

			casWfList.add(casWf);
		}
		// set wordforms
		word.setWordforms(FSUtils.toFSArray(jcas, casWfList));

		word.addToIndexes();
	}

	@Override
	public void apply(JCas jcas, Annotation token, Integer lexemeId, String lemma, BitSet posBits) {
		// TODO
		throw new UnsupportedOperationException();
	}

}