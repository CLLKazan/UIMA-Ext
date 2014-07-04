/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import opennlp.tools.util.SequenceValidator;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.PM;
import ru.kfu.cll.uima.tokenizer.fstype.SPECIAL;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PunctuationTokenSequenceValidator implements SequenceValidator<Token> {

	@Override
	public boolean validSequence(int i, Token[] inputSequence, String[] outcomesSequence,
			String outcome) {
		Token curToken = inputSequence[i];
		return checkForPunctuationTag(curToken, outcome);
	}

	public static boolean checkForPunctuationTag(Token curToken, String curOutcome) {
		boolean isPunctuationTag = PunctuationUtils.isPunctuationTag(curOutcome);
		// TODO
		if (curToken instanceof W || curToken instanceof NUM) {
			return !isPunctuationTag;
		} else if (curToken instanceof PM || curToken instanceof SPECIAL) {
			// curToken is a punctuation or a special symbol
			return isPunctuationTag;
		}
		return true;
	}
}
