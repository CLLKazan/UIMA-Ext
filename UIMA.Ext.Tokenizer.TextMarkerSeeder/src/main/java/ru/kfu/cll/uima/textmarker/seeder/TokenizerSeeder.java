/**
 * 
 */
package ru.kfu.cll.uima.textmarker.seeder;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.textmarker.seed.TextMarkerAnnotationSeeder;

import ru.kfu.cll.uima.tokenizer.fstype.TokenBase;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TokenizerSeeder implements TextMarkerAnnotationSeeder {
	@Override
	public Type seed(String text, CAS cas) {
		String tokenBaseTypeName = TokenBase.class.getName();
		Type tokenBaseType = cas.getTypeSystem().getType(tokenBaseTypeName);
		if (tokenBaseType == null) {
			throw new IllegalStateException(String.format(
					"Unknown type : %s", tokenBaseTypeName));
		}
		return tokenBaseType;
	}
}