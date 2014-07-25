/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger;

import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.uimafit.factory.TypeSystemDescriptionFactory;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PosTaggerAPI {

	public static final String TYPESYSTEM_POSTAGGER = "org.opencorpora.morphology-ts";
	
	public static final String AE_POSTAGGER = "ru.kfu.itis.issst.uima.postagger.postagger-ae";

	public static TypeSystemDescription getTypeSystemDescription() {
		return TypeSystemDescriptionFactory.createTypeSystemDescription(TYPESYSTEM_POSTAGGER);
	}

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
