/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform.allGramBitsFunction;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryUtils.toGramBits;

import java.util.BitSet;
import java.util.List;

import opennlp.tools.util.SequenceValidator;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.initialize.ExternalResourceInitializer;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.factory.initializable.Initializable;

import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.issst.uima.morph.commons.DictionaryBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.PunctuationUtils;
import ru.kfu.itis.issst.uima.morph.commons.TagUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.WordUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryHolder;
import ru.ksu.niimm.cll.uima.morph.ruscorpora.RNCMorphConstants;
import ru.ksu.niimm.cll.uima.morph.util.BitUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictionaryGrammemeLevelTokenSequenceValidator
		implements SequenceValidator<Token>, Initializable {

	public static final String RESOURCE_MORPH_DICT = "morphDict";

	@ExternalResource(key = RESOURCE_MORPH_DICT, mandatory = true)
	private MorphDictionaryHolder morphDictionaryHolder;
	//
	private MorphDictionary morphDictionary;
	private GramModel gramModel;
	// 
	private List<BitSet> skipMasks;

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		ExternalResourceInitializer.initialize(ctx, this);
		morphDictionary = morphDictionaryHolder.getDictionary();
		gramModel = morphDictionary.getGramModel();
		// TODO this is application-specific tunings. Refactor them out
		skipMasks = Lists.newLinkedList();
		{
			BitSet mask = new BitSet();
			mask.set(gramModel.getGrammemNumId(MorphConstants.Abbr));
			skipMasks.add(mask);
		}
		{
			BitSet mask = new BitSet();
			mask.set(gramModel.getGrammemNumId(RNCMorphConstants.RNC_INIT));
			skipMasks.add(mask);
		}
		{
			BitSet mask = new BitSet();
			mask.set(gramModel.getGrammemNumId(MorphConstants.Prnt));
			skipMasks.add(mask);
		}
		skipMasks = ImmutableList.copyOf(skipMasks);
	}

	@Override
	public boolean validSequence(int i, Token[] inputSequence, String[] outcomesSequence,
			String outcome) {
		Token curToken = inputSequence[i];
		// do basic check
		if (!PunctuationTokenSequenceValidator.checkForPunctuationTag(curToken, outcome)) {
			return false;
		}
		// do not validate punctuation tags as it is done before
		if (PunctuationUtils.isPunctuationTag(outcome)) {
			return true;
		}
		outcome = TagUtils.postProcessExternalTag(outcome);
		// dictionary look-up 
		String tokenStr = curToken.getCoveredText();
		tokenStr = WordUtils.normalizeToDictionaryForm(tokenStr);
		List<Wordform> dictEntries = morphDictionary.getEntries(tokenStr);
		if (dictEntries == null || dictEntries.isEmpty()) {
			return !TagUtils.isClosedClassTag(outcome);
		}
		// dictEntries is not empty so null-tag is not valid in most cases
		if (outcome == null) {
			return false;
		}
		// parse tag
		// TODO do not rely on the specific implementation of TagMapper
		Iterable<String> candidateGrams = DictionaryBasedTagMapper.parseTag(outcome);
		BitSet candidateBS = toGramBits(gramModel, candidateGrams);
		for (BitSet sm : skipMasks) {
			if (BitUtils.contains(candidateBS, sm)) {
				return true;
			}
		}
		// check containment
		List<BitSet> dictBSes = Lists.transform(dictEntries, allGramBitsFunction(morphDictionary));
		for (BitSet de : dictBSes) {
			if (BitUtils.contains(de, candidateBS)) {
				return true;
			}
		}
		return false;
	}
}
