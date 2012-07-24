/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Lemma implements Serializable {

	private static final long serialVersionUID = 7426278009038784123L;
	private static final BitSet EMPTY_BITSET = new BitSet();

	public static Builder builder(MorphDictionary dict, int id) {
		return new Builder(dict, id);
	}

	public static class Builder {
		private Lemma instance = new Lemma();
		private MorphDictionary dict;

		public Builder(MorphDictionary dict, int id) {
			instance.id = id;
			this.dict = dict;
		}

		public int getLemmaId() {
			return instance.id;
		}

		public Builder setString(String string) {
			instance.string = string;
			return this;
		}

		public Builder addGrammeme(String gramId) {
			if (instance.grammems == null) {
				instance.grammems = new BitSet(dict.getGrammemMaxNumId() + 1);
			}
			int gramNumId = dict.getGrammemNumId(gramId);
			instance.grammems.set(gramNumId);
			return this;
		}

		public Builder addWordform(Wordform wf) {
			if (instance.wordforms == null) {
				instance.wordforms = Sets.newHashSetWithExpectedSize(8);
			}
			if (!Objects.equal(wf.getLemmaId(), instance.id)) {
				throw new IllegalStateException("Can't add WF with different lemma id");
			}
			instance.wordforms.add(wf);
			return this;
		}

		public Lemma build() {
			if (instance.string == null) {
				throw new IllegalStateException("'string' is null");
			}
			if (instance.grammems == null) {
				instance.grammems = EMPTY_BITSET;
			}

			if (instance.wordforms == null) {
				instance.wordforms = ImmutableSet.of();
			} else {
				instance.wordforms = ImmutableSet.copyOf(instance.wordforms);
			}
			return instance;
		}
	}

	private int id;
	private String string;
	private BitSet grammems;
	// TODO may be better to replace by list to decrease memory consumption
	private Set<Wordform> wordforms;

	public int getId() {
		return id;
	}

	public String getString() {
		return string;
	}

	public BitSet getGrammems() {
		return (BitSet) grammems.clone();
	}

	public Set<Wordform> getWordforms() {
		return wordforms;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append(string).append("id", id).toString();
	}
}