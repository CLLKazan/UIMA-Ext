/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import java.util.List;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import org.uimafit.util.FSCollectionFactory;

import com.google.common.collect.ImmutableSet;

import ru.kfu.itis.cll.uima.eval.cas.FSCasDirectory;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static ru.kfu.itis.cll.uima.cas.FSUtils.toList;
import static ru.kfu.itis.cll.uima.cas.FSUtils.toSet;
import static ru.kfu.itis.cll.uima.cas.FSUtils.toStringArray;
import static ru.kfu.itis.issst.uima.morph.model.MorphConstants.*;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FSCasDirectoryWithTagsFiltering extends FSCasDirectory {

	private static final Set<String> FILTERED_GRAM_TAGS = ImmutableSet.of(
			Prnt,
			Fixd,
			Qual, Poss,
			tran, intr,
			Name, Patr, Surn,
			Dist);

	private static final Set<String> CASE_TAGS = ImmutableSet.of(
			nomn, gent, gen1, gen2, datv, accs, ablt, loct, loc1, loc2, voct);
	private static final Set<String> ANIMACY_TAGS = ImmutableSet.of(anim, inan);
	private static final Set<String> GENDER_TAGS = ImmutableSet.of(femn, masc, neut);
	private static final Set<String> ADJ_GRAM_CLASSES = ImmutableSet.of(ADJF, ADJS, PRTF, PRTS);
	private static final Set<String> SHORT_ADJ_GRAM_CLASSES = ImmutableSet.of(ADJS, PRTS);

	private DirType dirType;

	@Override
	protected void postProcessCAS(CAS cas) {
		try {
			postProcessJCas(cas.getJCas());
		} catch (CASException e) {
			throw new IllegalStateException(e);
		}
	}

	private void postProcessJCas(JCas cas) {
		AnnotationIndex<Annotation> wordIdx = cas.getAnnotationIndex(Word.typeIndexID);
		List<Annotation> wordList = newArrayList(wordIdx);
		for (Annotation _wAnno : wordList) {
			Word word = (Word) _wAnno;
			if (word.getWordforms() != null) {
				for (Wordform wf : FSCollectionFactory.create(word.getWordforms(), Wordform.class)) {
					postProcess(cas, wf);
				}
			}
		}
	}

	private void postProcess(JCas jCas, Wordform wf) {
		// alignment rule 4
		if (isGoldDirectory() && PRED.equals(getGramClass(wf))) {
			changeGramClass(jCas, wf, ADVB);
		}
		//
		if (wf.getGrammems() != null) {
			// arguable alignment rule 6
			if (isGoldDirectory() && getGramClass(wf) == null
					&& toSet(wf.getGrammems()).contains(Prnt)) {
				changeGramClass(jCas, wf, ADVB);
			}
			List<String> tagList = newLinkedList(toList(wf.getGrammems()));
			if (filterGramTags(getGramClass(wf), tagList)) {
				if (tagList.isEmpty()) {
					wf.setGrammems(null);
				} else {
					wf.setGrammems(toStringArray(jCas, tagList));
				}
			}
		}
	}

	private void changeGramClass(JCas jCas, Wordform wf, final String newGC) {
		final String oldGC = getGramClass(wf);
		wf.setPos(newGC);
		Set<String> wfGrams = newLinkedHashSet();
		if (newGC != null) {
			wfGrams.add(newGC);
		}
		wfGrams.addAll(toSet(wf.getGrammems()));
		wfGrams.remove(oldGC);
		wf.setGrammems(toStringArray(jCas, wfGrams));
	}

	private String getGramClass(Wordform wf) {
		return wf.getPos();
	}

	private boolean filterGramTags(String gramClass, List<String> tagList) {
		// alignment rule 1
		boolean changed = tagList.removeAll(FILTERED_GRAM_TAGS);
		// alignment rule 2
		if (!NOUN.equals(gramClass)) {
			changed |= tagList.removeAll(ANIMACY_TAGS);
		}
		// alignment rule 3
		if (ADJ_GRAM_CLASSES.contains(gramClass) && tagList.contains(plur)) {
			changed |= tagList.removeAll(GENDER_TAGS);
		}
		// alignment rule 5
		if (isSystemDirectory() && SHORT_ADJ_GRAM_CLASSES.contains(gramClass)) {
			changed |= tagList.removeAll(CASE_TAGS);
		}
		return changed;
	}

	private static enum DirType {
		SYSTEM, GOLD;
	}

	private boolean isSystemDirectory() {
		if (dirType == null) {
			initDirType();
		}
		return dirType.equals(DirType.SYSTEM);
	}

	private boolean isGoldDirectory() {
		if (dirType == null) {
			initDirType();
		}
		return dirType.equals(DirType.GOLD);
	}

	private void initDirType() {
		if (beanName.startsWith("gold")) {
			dirType = DirType.GOLD;
		} else if (beanName.startsWith("system")) {
			dirType = DirType.SYSTEM;
		} else {
			throw new IllegalStateException("Can't assign DirType for bean named " + beanName);
		}
	}
}