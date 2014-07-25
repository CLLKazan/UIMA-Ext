/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils.punctuationTagMap;
import static ru.kfu.itis.issst.uima.morph.model.MorphConstants.*;

import java.util.BitSet;
import java.util.Set;

import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;

import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * EXPERIMENTAL <br>
 * EXPERIMENTAL <br>
 * EXPERIMENTAL
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TagUtils {

	private static final Set<String> closedPosSet = ImmutableSet.of(NPRO, Apro, PREP, CONJ, PRCL);

	/**
	 * @param dict
	 * @return function that returns true if the given gram bits represents a
	 *         closed class tag
	 */
	public static Function<BitSet, Boolean> getClosedClassIndicator(GramModel gm) {
		// initialize mask
		final BitSet closedClassTagsMask = new BitSet();
		for (String cpGram : closedPosSet) {
			closedClassTagsMask.set(gm.getGrammemNumId(cpGram));
		}
		//
		return new Function<BitSet, Boolean>() {
			@Override
			public Boolean apply(BitSet _wfBits) {
				BitSet wfBits = (BitSet) _wfBits.clone();
				wfBits.and(closedClassTagsMask);
				return !wfBits.isEmpty();
			}

		};
	}

	// FIXME refactor hard-coded dependency on a tag mapper implementation
	public static boolean isClosedClassTag(String tag) {
		return closedClassPunctuationTags.contains(tag)
				|| !Sets.intersection(
						GramModelBasedTagMapper.parseTag(tag), closedPosSet)
						.isEmpty();
	}

	public static String postProcessExternalTag(String tag) {
		return !"null".equals(String.valueOf(tag)) ? tag : null;
	}

	public static final Set<String> closedClassPunctuationTags = ImmutableSet
			.copyOf(punctuationTagMap.values());

	public static final Function<Word, String> tagFunction() {
		return tagFunction;
	}

	private static final Function<Word, String> tagFunction = new Function<Word, String>() {
		@Override
		public String apply(Word word) {
			if (word == null) {
				return null;
			}
			Wordform wf = MorphCasUtils.requireOnlyWordform(word);
			return wf.getPos();
		}
	};

	private TagUtils() {
	}
}
