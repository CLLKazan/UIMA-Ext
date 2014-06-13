/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static ru.ksu.niimm.cll.uima.morph.ruscorpora.RNCMorphConstants.*;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DictionaryExtensionBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModelPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LemmaPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.YoLemmaPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ImmutableGramModel.Builder;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class RNCDictionaryExtension extends DictionaryExtensionBase {

	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public List<LemmaPostProcessor> getLexemePostprocessors() {
		return Arrays.<LemmaPostProcessor> asList(
				YoLemmaPostProcessor.INSTANCE,
				predProcessor
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

	private final LemmaPostProcessor predProcessor = new LemmaPostProcessor() {

		@Override
		public boolean process(MorphDictionary dict, Lemma.Builder lemmaBuilder,
				Multimap<String, Wordform> wfMap) {
			GramModel gm = dict.getGramModel();
			final int predId = gm.getGrammemNumId(PRED);
			final BitSet tenseCat = gm.getGrammemWithChildrenBits(TEns, true);
			BitSet lemmaGramsBackup = (BitSet) lemmaBuilder.getGrammems().clone();
			if (lemmaBuilder.getGrammems().get(predId)) {
				lemmaBuilder.getGrammems().andNot(tenseCat);
			}
			if (!lemmaGramsBackup.equals(lemmaBuilder.getGrammems())) {
				logModification("PRED-TEns : %s", lemmaBuilder.getString());
			}
			return true;
		}
	};

	private void logModification(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}
}
