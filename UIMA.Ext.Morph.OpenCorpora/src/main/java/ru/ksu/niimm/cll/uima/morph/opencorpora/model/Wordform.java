/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora.model;

import java.io.Serializable;
import java.util.BitSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class Wordform implements Serializable {

	private static final long serialVersionUID = -8435061415886880938L;
	private static final BitSet EMPTY_BITSET = new BitSet();

	public static Builder builder(MorphDictionary dict, int lemmaId) {
		return new Builder(dict, lemmaId);
	}

	public static class Builder {
		private Wordform instance = new Wordform();
		private MorphDictionary dict;

		public Builder(MorphDictionary dict, int lemmaId) {
			instance.lemmaId = lemmaId;
			this.dict = dict;
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

		public Wordform build() {
			if (instance.string == null) {
				throw new IllegalStateException("'string' value is mandatory");
			}
			if (instance.grammems == null) {
				instance.grammems = EMPTY_BITSET;
			}
			return instance;
		}
	}

	private int lemmaId;
	private String string;
	private BitSet grammems;

	private Wordform() {
	}

	public int getLemmaId() {
		return lemmaId;
	}

	public String getString() {
		return string;
	}

	public BitSet getGrammems() {
		// !!!
		return (BitSet) grammems.clone();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(lemmaId).append(string).append(grammems).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Wordform)) {
			return false;
		}
		Wordform that = (Wordform) obj;
		return new EqualsBuilder().append(this.lemmaId, that.lemmaId)
				.append(this.string, that.string)
				.append(this.grammems, that.grammems)
				.isEquals();
	}
}