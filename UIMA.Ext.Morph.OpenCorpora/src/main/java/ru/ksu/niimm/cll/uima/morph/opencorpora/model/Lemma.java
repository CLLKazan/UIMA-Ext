/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

import java.io.Serializable;
import java.util.BitSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

/**
 * TODO rename to Lexeme
 * 
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

		public Lemma build() {
			if (instance.string == null) {
				throw new IllegalStateException("'string' is null");
			}
			if (instance.grammems == null) {
				instance.grammems = EMPTY_BITSET;
			}
			return instance;
		}
	}

	private int id;
	private String string;
	private BitSet grammems;

	public Lemma() {
	}

	public Lemma(String string, BitSet grammems) {
		this.string = string;
		this.grammems = grammems;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getString() {
		return string;
	}

	public BitSet getGrammems() {
		return (BitSet) grammems.clone();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append(string).append("id", id).toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(string).append(grammems).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Lemma)) {
			return false;
		}
		Lemma that = (Lemma) obj;
		return new EqualsBuilder().append(this.string, that.string)
				.append(this.grammems, that.grammems).isEquals();
	}

	public Lemma cloneWithoutIdAndString() {
		return new Lemma("", grammems);
	}

	public Lemma cloneWithGrammems(BitSet grammems) {
		Lemma result = new Lemma();
		result.id = this.id;
		result.string = this.string;
		result.grammems = grammems;
		return result;
	}
}