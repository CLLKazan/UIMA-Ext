/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import java.util.Set;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.opencorpora.cas.Wordform;

import ru.kfu.itis.cll.uima.cas.FSUtils;

import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class IdentityTagTagger implements RusCorporaTagMapper {

	@Override
	public void mapFromRusCorpora(RusCorporaWordform srcWf, Wordform targetWf) {
		JCas jCas;
		try {
			jCas = targetWf.getCAS().getJCas();
		} catch (CASException e) {
			throw new RuntimeException(e);
		}
		targetWf.setPos(srcWf.getPos());
		targetWf.setLemma(srcWf.getLex());
		Set<String> resultGrams = Sets.newLinkedHashSet();
		resultGrams.addAll(srcWf.getLexGrammems());
		resultGrams.addAll(srcWf.getWordformGrammems());
		if (!resultGrams.isEmpty()) {
			targetWf.setGrammems(FSUtils.toStringArray(jCas, resultGrams));
		}
	}

}