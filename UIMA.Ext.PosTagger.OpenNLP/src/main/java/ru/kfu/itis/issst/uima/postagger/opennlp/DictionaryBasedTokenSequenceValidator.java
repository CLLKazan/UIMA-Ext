/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.util.Arrays;

import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.SequenceValidator;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryBasedTokenSequenceValidator implements SequenceValidator<Token> {

	private final TagDictionary tagDict;

	public DictionaryBasedTokenSequenceValidator(TagDictionary tagDict) {
		this.tagDict = tagDict;
	}

	@Override
	public boolean validSequence(int i, Token[] inputSequence, String[] outcomesSequence,
			String outcome) {
		if (tagDict == null) {
			return true;
		}
		String tokenStr = inputSequence[i].getCoveredText();
		tokenStr = WordUtils.normalizeToDictionaryForm(tokenStr);
		String[] tags = tagDict.getTags(tokenStr);
		if (tags == null) {
			return true;
		} else {
			return Arrays.asList(tags).contains(outcome);
		}
	}

}
