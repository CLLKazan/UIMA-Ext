/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static ru.ksu.niimm.cll.uima.morph.ruscorpora.RNCMorphConstants.*;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;

import java.util.Arrays;
import java.util.List;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DictionaryExtensionBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModelPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LemmaPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.YoLemmaPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ImmutableGramModel.Builder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class RNCDictionaryExtension extends DictionaryExtensionBase {
	
	@Override
	public List<LemmaPostProcessor> getLexemePostprocessors() {
		return Arrays.<LemmaPostProcessor> asList(
				YoLemmaPostProcessor.INSTANCE
				// XXX
				);
	}

	@Override
	public List<GramModelPostProcessor> getGramModelPostProcessors() {
		GramModelPostProcessor rncGramsAdder = new GramModelPostProcessor() {
			@Override
			public void postprocess(Builder gmBuilder) {
				gmBuilder.addGrammeme(new Grammeme(
						RNC_INIT, POST, RNC_INIT, "Initial Letter in RNC"));
				gmBuilder.addGrammeme(new Grammeme(
						RNC_Abbr, null, RNC_Abbr, "Abbreviation in RNC"));
			}
		};
		return Arrays.asList(rncGramsAdder);
	}

}
