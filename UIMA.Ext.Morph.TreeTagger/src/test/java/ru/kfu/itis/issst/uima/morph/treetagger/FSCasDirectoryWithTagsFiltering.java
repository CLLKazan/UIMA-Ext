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
import com.google.common.collect.Lists;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.kfu.itis.cll.uima.eval.cas.FSCasDirectory;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FSCasDirectoryWithTagsFiltering extends FSCasDirectory {

	private static final Set<String> FILTERED_GRAM_TAGS = ImmutableSet.of(
			Fixd,
			Qual, Poss,
			tran, intr);

	private static final Set<String> ANIMACY_TAGS = ImmutableSet.of(anim, inan);

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
		List<Annotation> wordList = Lists.newArrayList(wordIdx);
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
		if (wf.getGrammems() != null) {
			List<String> tagList = Lists.newLinkedList(FSUtils.toList(wf.getGrammems()));
			if (filterGramTags(wf.getPos(), tagList)) {
				if (tagList.isEmpty()) {
					wf.setGrammems(null);
				} else {
					wf.setGrammems(FSUtils.toStringArray(jCas, tagList));
				}
			}
		}
	}

	private boolean filterGramTags(String pos, List<String> tagList) {
		boolean changed = tagList.removeAll(FILTERED_GRAM_TAGS);
		if (!NOUN.equals(pos)) {
			changed |= tagList.removeAll(ANIMACY_TAGS);
		}
		return changed;
	}
}