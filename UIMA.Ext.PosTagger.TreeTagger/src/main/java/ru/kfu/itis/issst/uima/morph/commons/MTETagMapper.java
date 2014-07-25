/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.commons;

import static com.google.common.collect.Tables.unmodifiableRowSortedTable;
import static ru.kfu.itis.issst.uima.morph.model.MorphConstants.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.RowSortedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MTETagMapper implements TagMapper {

	private static Map<Character, RowSortedTable<Integer, Character, TagCodeHandler>> cat2Table;

	private static final TagCodeHandler noOp = new TagCodeHandler() {
		@Override
		public void apply(WordformBuilder wb) {
		}
	};

	static {
		cat2Table = Maps.newHashMap();
		RowSortedTable<Integer, Character, TagCodeHandler> table;
		// NOUN
		table = TreeBasedTable.create();
		table.put(0, 'N', setPos(NOUN));
		// type
		table.put(1, 'c', noOp);
		table.put(1, 'p', noOp);
		// gender
		table.put(2, 'm', addGram(masc));
		table.put(2, 'f', addGram(femn));
		table.put(2, 'n', addGram(neut));
		table.put(2, 'c', addGram(GNdr, comgend));
		table.put(2, '-', addGram(GNdr));
		// number
		table.put(3, '-', noOp);
		table.put(3, 's', addGram(sing));
		table.put(3, 'p', addGram(plur));
		// case
		table.put(4, 'n', addGram(nomn));
		table.put(4, 'g', addGram(gent));
		table.put(4, 'd', addGram(datv));
		table.put(4, 'a', addGram(accs));
		table.put(4, 'v', addGram(voct));
		table.put(4, 'l', addGram(loct));
		table.put(4, 'i', addGram(ablt));
		// animate
		table.put(5, 'n', addGram(inan));
		table.put(5, 'y', addGram(anim));
		table.put(5, '-', addGram(ANim));
		// case-mod
		table.put(6, 'p', and(removeGram(nomn, gent, datv, accs, voct, loct, ablt), addGram(gen2)));
		table.put(6, 'l', and(removeGram(nomn, gent, datv, accs, voct, loct, ablt), addGram(loc2)));
		cat2Table.put('N', Tables.unmodifiableRowSortedTable(table));
		// -------------------------------------------------------
		// VERB
		// -------------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'V', setPos(VERB));
		// type
		table.put(1, 'm', noOp);
		table.put(1, 'a', noOp);
		// vform
		table.put(2, '-', noOp);
		table.put(2, 'i', addGram(indc));
		table.put(2, 'm', addGram(impr));
		// table.put(2, 'c', TODO);
		table.put(2, 'n', setPos(INFN));
		table.put(2, 'p', setPos(PRTF));
		table.put(2, 'g', setPos(GRND));
		// tense
		table.put(3, '-', noOp);
		table.put(3, 'p', addGram(pres));
		table.put(3, 'f', addGram(futr));
		table.put(3, 's', addGram(past));
		// person
		table.put(4, '-', noOp);
		table.put(4, '1', addGram(per1));
		table.put(4, '2', addGram(per2));
		table.put(4, '3', addGram(per3));
		// number
		table.put(5, '-', noOp);
		table.put(5, 's', addGram(sing));
		table.put(5, 'p', addGram(plur));
		// gender
		table.put(6, '-', noOp);
		table.put(6, 'm', addGram(masc));
		table.put(6, 'f', addGram(femn));
		table.put(6, 'n', addGram(neut));
		// voice
		table.put(7, '-', noOp);
		// TODO ignore 'medial' in evaluation
		table.put(7, 'm', ifNotContain(addGram(actv, pssv), VERB, INFN, GRND));
		table.put(7, 'a', ifNotContain(addGram(actv), VERB, INFN, GRND));
		table.put(7, 'p', ifNotContain(addGram(pssv), VERB, INFN, GRND));
		// definiteness
		table.put(8, '-', noOp);
		table.put(8, 's', setPos(PRTS));
		table.put(8, 'f', setPos(PRTF));
		// aspect
		table.put(9, '-', noOp);
		// XXX this is contradictory with Rus MTE docs, but seems to give
		// much more accurate mapping
		table.put(9, 'p', addGram(perf));
		table.put(9, 'e', addGram(impf));
		// TODO ignore in evaluation 
		table.put(9, 'b', addGram(perf, impf));
		// case
		table.put(10, '-', noOp);
		table.put(10, 'n', addGram(nomn));
		table.put(10, 'g', addGram(gent));
		table.put(10, 'd', addGram(datv));
		table.put(10, 'a', addGram(accs));
		table.put(10, 'l', addGram(loct));
		table.put(10, 'i', addGram(ablt));
		cat2Table.put('V', Tables.unmodifiableRowSortedTable(table));
		// -------------------------------------------------------
		// ADJECTIVE
		// -------------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'A', setPos(ADJF));
		// type
		table.put(1, '-', noOp);
		table.put(1, 'f', addGram(Qual));
		table.put(1, 's', addGram(Poss));
		// degree
		table.put(2, '-', noOp);
		table.put(2, 'p', noOp);
		table.put(2, 'c', setPos(COMP));
		table.put(2, 's', addGram(Supr));
		// gender
		table.put(3, '-', noOp);
		table.put(3, 'm', addGram(masc));
		table.put(3, 'f', addGram(femn));
		table.put(3, 'n', addGram(neut));
		// number
		table.put(4, '-', noOp);
		table.put(4, 's', addGram(sing));
		table.put(4, 'p', addGram(plur));
		// case
		table.put(5, '-', noOp);
		table.put(5, 'n', addGram(nomn));
		table.put(5, 'g', addGram(gent));
		table.put(5, 'd', addGram(datv));
		table.put(5, 'a', addGram(accs));
		table.put(5, 'l', addGram(loct));
		table.put(5, 'i', addGram(ablt));
		// definiteness
		table.put(6, '-', noOp);
		table.put(6, 's', setPos(ADJS));
		table.put(6, 'f', setPos(ADJF));
		cat2Table.put('A', Tables.unmodifiableRowSortedTable(table));
		// ---------------------------------------------
		// PRONOUN
		// ---------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'P', setPos(NPRO));
		// type
		table.put(1, '-', noOp);
		// seems like this code are not supported by model
		// person
		table.put(2, '-', noOp);
		table.put(2, '1', addGram(per1));
		table.put(2, '2', addGram(per2));
		table.put(2, '3', addGram(per3));
		// gender
		table.put(3, '-', noOp);
		table.put(3, 'm', addGram(masc));
		table.put(3, 'f', addGram(femn));
		table.put(3, 'n', addGram(neut));
		// number
		table.put(4, '-', noOp);
		table.put(4, 's', addGram(sing));
		table.put(4, 'p', addGram(plur));
		// case
		table.put(5, '-', noOp);
		table.put(5, 'n', addGram(nomn));
		table.put(5, 'g', addGram(gent));
		table.put(5, 'd', addGram(datv));
		table.put(5, 'a', addGram(accs));
		table.put(5, 'v', addGram(voct));
		table.put(5, 'l', addGram(loct));
		table.put(5, 'i', addGram(ablt));
		// syntactic_type
		table.put(6, '-', noOp);
		table.put(6, 'n', noOp);
		table.put(6, 'a', and(setPos(ADJF), addGram(Apro)));
		table.put(6, 'r', and(setPos(ADVB), addGram(Apro)));
		// animate
		table.put(7, '-', noOp);
		table.put(7, 'n', addGram(inan));
		table.put(7, 'y', addGram(anim));
		cat2Table.put('P', unmodifiableRowSortedTable(table));
		// ----------------------------------------------------------
		// ADVERB
		// ----------------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'R', setPos(ADVB));
		// degree
		table.put(1, '-', noOp);
		table.put(1, 'p', noOp);
		table.put(1, 'c', setPos(COMP));
		table.put(1, 's', addGram(Supr));
		cat2Table.put('R', unmodifiableRowSortedTable(table));
		// -----------------------------------------------------------
		// ADPOSITION
		// -----------------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'S', setPos(PREP));
		// type
		table.put(1, 'p', noOp);
		// formation
		table.put(2, '-', noOp);
		table.put(2, 's', noOp);
		table.put(2, 'c', noOp);
		// case 
		table.put(3, '-', noOp);
		// preposition for nominative case?!?
		table.put(3, 'n', noOp);
		table.put(3, 'g', noOp);
		table.put(3, 'd', noOp);
		table.put(3, 'a', noOp);
		table.put(3, 'l', noOp);
		table.put(3, 'i', noOp);
		cat2Table.put('S', unmodifiableRowSortedTable(table));
		// ---------------------------------------------------
		// CONJUNCTION
		// ---------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'C', setPos(CONJ));
		// type
		table.put(1, 'c', noOp);
		table.put(1, '-', noOp);
		table.put(1, 's', noOp);
		// formation
		table.put(2, '-', noOp);
		table.put(2, 's', noOp);
		table.put(2, 'c', noOp);
		// coord type
		table.put(3, '-', noOp);
		table.put(3, 'p', noOp);
		table.put(3, 'w', noOp);
		// subtype
		table.put(4, '-', noOp);
		table.put(4, 'z', noOp);
		table.put(4, 'p', noOp);
		cat2Table.put('C', unmodifiableRowSortedTable(table));
		// ---------------------------------------------------
		// NUMERAL
		// ---------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'M', setPos(NUMR));
		// type
		table.put(1, 'c', noOp);
		table.put(1, 'o', and(setPos(ADJF), addGram(Anum)));
		// gender
		table.put(2, '-', noOp);
		table.put(2, 'm', addGram(masc));
		table.put(2, 'f', addGram(femn));
		table.put(2, 'n', addGram(neut));
		// number
		table.put(3, '-', noOp);
		table.put(3, 's', addGram(sing));
		table.put(3, 'p', addGram(plur));
		// case
		table.put(4, '-', noOp);
		table.put(4, 'n', addGram(nomn));
		table.put(4, 'g', addGram(gent));
		table.put(4, 'd', addGram(datv));
		table.put(4, 'a', addGram(accs));
		table.put(4, 'l', addGram(loct));
		table.put(4, 'i', addGram(ablt));
		// ?unknown?
		table.put(5, 'd', noOp); // possibly, it states for a digital representation
		cat2Table.put('M', unmodifiableRowSortedTable(table));
		// ---------------------------------------------------
		// PARTICLE
		// ---------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'Q', setPos(PRCL));
		// formation
		table.put(1, '-', noOp);
		table.put(1, 's', noOp);
		table.put(1, 'c', noOp);
		cat2Table.put('Q', unmodifiableRowSortedTable(table));
		// ---------------------------------------------------
		// INTERJECTION
		// ---------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'I', setPos(INTJ));
		// formation
		table.put(1, '-', noOp);
		table.put(1, 's', noOp);
		table.put(1, 'c', noOp);
		cat2Table.put('I', unmodifiableRowSortedTable(table));
		// ---------------------------------------------------
		// ABBREVIATION
		// ---------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'Y', setPos(NOUN));
		// syntactic type
		table.put(1, '-', noOp);
		table.put(1, 'n', noOp);
		table.put(1, 'r', noOp);
		// gender
		table.put(2, 'm', addGram(masc));
		table.put(2, 'f', addGram(femn));
		table.put(2, 'n', addGram(neut));
		table.put(2, '-', addGram(GNdr));
		// number
		table.put(3, '-', noOp);
		table.put(3, 's', addGram(sing));
		table.put(3, 'p', addGram(plur));
		// case
		table.put(4, 'n', addGram(nomn));
		table.put(4, 'g', addGram(gent));
		table.put(4, 'd', addGram(datv));
		table.put(4, 'a', addGram(accs));
		table.put(4, 'v', addGram(voct));
		table.put(4, 'l', addGram(loct));
		table.put(4, 'i', addGram(ablt));
		cat2Table.put('Y', unmodifiableRowSortedTable(table));
		// ---------------------------------------------------
		// RESIDUAL
		// ---------------------------------------------------
		table = TreeBasedTable.create();
		table.put(0, 'X', setPos("RESIDUAL"));
		cat2Table.put('X', unmodifiableRowSortedTable(table));
		// finally
		cat2Table = ImmutableMap.copyOf(cat2Table);
	}

	private static TagCodeHandler and(final TagCodeHandler... handlers) {
		if (handlers.length == 0) {
			throw new IllegalArgumentException();
		}
		return new TagCodeHandler() {
			@Override
			public void apply(WordformBuilder wb) {
				for (TagCodeHandler h : handlers) {
					h.apply(wb);
				}
			}
		};
	}

	private static TagCodeHandler ifNotContain(final TagCodeHandler nested, String... grs) {
		if (grs.length == 0) {
			throw new IllegalArgumentException();
		}
		final ImmutableSet<String> grSet = ImmutableSet.copyOf(grs);
		return new TagCodeHandler() {
			@Override
			public void apply(WordformBuilder wb) {
				for (String grToTest : wb.grammems) {
					if (grSet.contains(grToTest)) {
						return;
					}
				}
				nested.apply(wb);
			}
		};
	}

	private static TagCodeHandler removeGram(final String... grs) {
		if (grs.length == 0) {
			throw new IllegalArgumentException();
		}
		return new TagCodeHandler() {
			@Override
			public void apply(WordformBuilder wb) {
				HashSet<String> grsSet = Sets.newHashSet(grs);
				wb.grammems.removeAll(grsSet);
			}
		};
	}

	private static TagCodeHandler addGram(final String... grs) {
		if (grs.length == 0) {
			throw new IllegalArgumentException();
		}
		return new TagCodeHandler() {
			@Override
			public void apply(WordformBuilder wb) {
				for (String gr : grs) {
					wb.addGram(gr);
				}
			}
		};
	}

	private static TagCodeHandler setPos(final String pos) {
		return new TagCodeHandler() {
			@Override
			public void apply(WordformBuilder wb) {
				wb.setPos(pos);
			}
		};
	}

	private Logger log = LoggerFactory.getLogger(getClass());

	private static final Set<String> TAGS_TO_IGNORE = ImmutableSet.of("-", "SENT");

	@Override
	public Set<String> parseTag(String tag, String token) {
		if (tag == null || tag.isEmpty()) {
			log.warn("Empty tag '%s' has been returned for token {}", tag, token);
			return null;
		}
		if (TAGS_TO_IGNORE.contains(tag)) {
			log.debug("Can't parse token '{}'", token);
			return null;
		}
		char category = tag.charAt(0);
		RowSortedTable<Integer, Character, TagCodeHandler> catTable = cat2Table.get(category);
		if (catTable == null) {
			throw new IllegalArgumentException(String.format(
					"Unknown category '%s' in tag '%s' for token %s", category, tag, token));
		}
		if (tag.length() - 1 > catTable.rowKeySet().last()) {
			throw new IllegalArgumentException(String.format(
					"Too much positions in tag '%s' for token %s", tag, token));
		}
		// build wordform
		WordformBuilder wb = new WordformBuilder();
		// invoke value-independent handlers
		for (TagCodeHandler tch : catTable.row(0).values()) {
			tch.apply(wb);
		}
		// invoke handlers for each position
		for (int i = 1; i < tag.length(); i++) {
			TagCodeHandler tch = catTable.get(i, tag.charAt(i));
			if (tch == null) {
				throw new IllegalArgumentException(String.format(
						"Unknown code at position %s in tag '%s' for token '%s'",
						i, tag, token));
			}
			tch.apply(wb);
		}
		// fill FS
		if (wb.pos == null) {
			throw new IllegalStateException(String.format(
					"Empty pos for token '%s' with tag '%s'", token, tag));
		}
		return wb.grammems;
	}

	static interface TagCodeHandler {
		void apply(WordformBuilder wb);
	}

	static class WordformBuilder {
		private Set<String> grammems = Sets.newLinkedHashSet();
		private String pos;

		void addGram(String gr) {
			grammems.add(gr);
		}

		void setPos(String pos) {
			if (this.pos != null) {
				// remove old
				grammems.remove(this.pos);
			}
			this.pos = pos;
			if (pos != null) {
				grammems.add(pos);
			}
		}
	}

	@Override
	public String toTag(Set<String> grams) {
		throw new UnsupportedOperationException();
	}
}
