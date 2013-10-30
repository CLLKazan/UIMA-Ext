/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import java.util.LinkedHashSet;

import org.apache.uima.jcas.JCas;
import org.opencorpora.cas.Wordform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import ru.kfu.itis.cll.uima.cas.FSUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphCasUtils {

	public static void addGrammeme(JCas jCas, Wordform wf, String newGram) {
		addGrammemes(jCas, wf, ImmutableList.of(newGram));
	}

	public static void addGrammemes(JCas jCas, Wordform wf, Iterable<String> newGrams) {
		LinkedHashSet<String> wfGrams = Sets.newLinkedHashSet(FSUtils.toSet(wf.getGrammems()));
		boolean changed = false;
		for (String newGram : newGrams) {
			changed |= wfGrams.add(newGram);
		}
		if (changed) {
			wf.setGrammems(FSUtils.toStringArray(jCas, wfGrams));
		}
	}

	private MorphCasUtils() {
	}

}