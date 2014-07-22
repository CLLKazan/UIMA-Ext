/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger;

import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.impl.Import_impl;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTaggerAPI {

	public static final String AE_POSTAGGER = "ru.kfu.itis.issst.uima.postagger.postagger-ae";

	public static Import getAEImport() {
		Import result = new Import_impl();
		result.setName(AE_POSTAGGER);
		return result;
	}

	/*
	 * TODO:LOW
	 * 1) This is a wrong place for this method
	 * 2) Also the semantic of this method forces rather rigid constraints on PosTagger output
	 * But until a bright future we have to keep different implementations work in the coherent manner.   
	 */
	public static boolean canCarryWord(Token token) {
		return token instanceof W || token instanceof NUM;
	}

	private PosTaggerAPI() {
	}
}
