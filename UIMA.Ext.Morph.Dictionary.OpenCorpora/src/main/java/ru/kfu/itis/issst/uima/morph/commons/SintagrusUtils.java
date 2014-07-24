/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import java.util.List;
import java.util.Set;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class SintagrusUtils {

	public static Set<String> mapToOpenCorpora(Set<String> srcSet) {
		srcSet = Sets.newHashSet(srcSet);
		Set<String> result = Sets.newHashSet();
		for (Submapper sm : submappers) {
			sm.apply(srcSet, result);
		}
		if (!srcSet.isEmpty()) {
			throw new IllegalArgumentException(String.format(
					"Unknown grammemes: %s", srcSet));
		}
		return result;
	}

	private static final List<Submapper> submappers;
	static {
		Builder<Submapper> b = ImmutableList.<Submapper> builder();
		// noun
		b.add(replace("S", NOUN));
		// adjectives
		b.add(replace(new String[] { "A", "СРАВ" }, COMP));
		b.add(replace(new String[] { "A", "КР" }, ADJS));
		b.add(replace("A", ADJF));
		// verbs
		b.add(replace(new String[] { "V", "ПРИЧ", "КР" }, PRTS));
		b.add(replace(new String[] { "V", "ПРИЧ" }, PRTF));
		b.add(replace(new String[] { "V", "ИНФ" }, INFN));
		b.add(replace(new String[] { "V", "ДЕЕПР" }, GRND));
		b.add(replace("V", VERB));
		// adverb
		b.add(replace(new String[] { "ADV", "СРАВ" }, COMP));
		b.add(replace("ADV", ADVB));
		// numeral
		b.add(replace("NUM", NUMR));
		// preposition
		b.add(replace("PR", PREP));
		// TODO COM
		// conjunction
		b.add(replace("CONJ", CONJ));
		// particle
		b.add(replace("PART", PRCL));
		// P
		b.add(replace("P", PRCL));
		// interjection
		b.add(replace("INTJ", INTJ));
		// NID
		b.add(replace("NID"));
		// TODO pronouns
		// -ANIMACY
		b.add(replace("ОД", anim));
		b.add(replace("НЕОД", inan));
		// TODO handle ANIMACY in ADJF & PRTF
		// -GENDER-
		b.add(replace("МУЖ", masc));
		b.add(replace("ЖЕН", femn));
		b.add(replace("СРЕД", neut));
		// -NUMBER-
		b.add(replace("ЕД", sing));
		b.add(replace("МН", plur));
		// -CASE-
		b.add(replace("ИМ", nomn));
		b.add(replace("РОД", gent));
		b.add(replace("ПАРТ", gen2));
		b.add(replace("ДАТ", datv));
		b.add(replace("ВИН", accs));
		b.add(replace("ТВОР", ablt));
		b.add(replace("ПР", loct));
		b.add(replace("МЕСТН", loc2));
		b.add(replace("ЗВ", voct));
		// -comparatives-
		b.add(replace("ПРЕВ", Supr));
		// -MOod-
		b.add(replace("ИЗЪЯВ", indc));
		b.add(replace("ПОВ", impr));
		// -aspect-
		b.add(replace("НЕСОВ", impf));
		b.add(replace("СОВ", perf));
		// -tense-
		// XXX b.add(replace("непрош"))
		b.add(replace("ПРОШ", past));
		b.add(replace("НАСТ", pres));
		// -person-
		b.add(replace("1-Л", per1));
		b.add(replace("2-Л", per2));
		b.add(replace("3-Л", per3));
		// -voice-
		b.add(replace("страд", pssv));
		// aux
		submappers = b.build();
	}

	private static Submapper replace(String what, String... replacement) {
		return new ReplaceSubmapper(ImmutableSet.of(what),
				ImmutableSet.copyOf(replacement));
	}

	private static Submapper replace(String[] what, String... replacement) {
		return new ReplaceSubmapper(ImmutableSet.copyOf(what),
				ImmutableSet.copyOf(replacement));
	}

	private static interface Submapper {
		void apply(Set<String> src, Set<String> target);
	}

	private static class ReplaceSubmapper implements Submapper {
		private final Set<String> what;
		private final Set<String> replacement;

		private ReplaceSubmapper(Set<String> what, Set<String> replacement) {
			this.what = what;
			this.replacement = replacement;
		}

		@Override
		public void apply(Set<String> src, Set<String> target) {
			if (src.containsAll(what)) {
				target.addAll(replacement);
				src.removeAll(what);
			}
		}
	}

	private SintagrusUtils() {
	}

}
