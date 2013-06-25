/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class RusCorporaWordform extends RusCorporaAnnotation {

	private String lex;
	private String pos;
	private Set<String> lexGrammems;
	private Set<String> wordformGrammems;

	public RusCorporaWordform(int begin) {
		super(begin);
	}

	public RusCorporaWordform(int begin, int end) {
		super(begin, end);
	}

	public String getLex() {
		return lex;
	}

	public void setLex(String lex) {
		this.lex = lex;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public Set<String> getLexGrammems() {
		return lexGrammems;
	}

	public void setLexGrammems(Set<String> lexGrammems) {
		this.lexGrammems = lexGrammems;
	}

	public Set<String> getWordformGrammems() {
		return wordformGrammems;
	}

	public void setWordformGrammems(Set<String> wordformGrammems) {
		this.wordformGrammems = wordformGrammems;
	}

	public Set<String> getAllGrammems() {
		return Sets.union(lexGrammems, wordformGrammems);
	}
}