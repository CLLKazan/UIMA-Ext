/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.ruscorpora;

import static ru.ksu.niimm.cll.uima.morph.ruscorpora.RNCMorphConstants.*;
import static ru.ksu.niimm.cll.uima.morph.opencorpora.model.MorphConstants.*;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Grammeme;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma.Builder;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.DictionaryExtensionBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModel;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.GramModelPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ImmutableGramModel;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LemmaPostProcessor;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.LexemePostProcessorBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionaryImpl;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.YoLemmaPostProcessor;

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
				predProcessor,
				proADJFAsNPRO,
				advbAsPred,
				subcaseProcessor
				// XXX
				);
	}

	@Override
	public List<GramModelPostProcessor> getGramModelPostProcessors() {
		GramModelPostProcessor rncGramsAdder = new GramModelPostProcessor() {
			@Override
			public void postprocess(ImmutableGramModel.Builder gmBuilder) {
				gmBuilder.addGrammeme(new Grammeme(
						RNC_INIT, POST, RNC_INIT, "Initial Letter in RNC"));
				gmBuilder.addGrammeme(new Grammeme(
						RNC_Abbr, null, RNC_Abbr, "Abbreviation in RNC"));
			}
		};
		return Arrays.asList(rncGramsAdder);
	}

	private final LemmaPostProcessor predProcessor = new LexemePostProcessorBase() {

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

	private final LemmaPostProcessor proADJFAsNPRO = new GeneratingLexemePostProcessorBase() {
		@Override
		public boolean process(MorphDictionary dict, Lemma.Builder lemmaBuilder,
				Multimap<String, Wordform> wfMap) {
			int adjfId = dict.getGramModel().getGrammemNumId(ADJF);
			int aproId = dict.getGramModel().getGrammemNumId(Apro);
			BitSet lemmaGrams = lemmaBuilder.getGrammems();
			if (lemmaGrams.get(adjfId) && lemmaGrams.get(aproId)) {
				int nproId = dict.getGramModel().getGrammemNumId(NPRO);
				Lemma.Builder newLemma = lemmaBuilder.copy(-1);
				newLemma.getGrammems().clear(adjfId);
				newLemma.getGrammems().clear(aproId);
				newLemma.getGrammems().set(nproId);
				add(newLemma, copyWordforms(wfMap));
			}
			return true;
		}
	};

	private final LemmaPostProcessor advbAsPred = new GeneratingLexemePostProcessorBase() {

		@Override
		public boolean process(MorphDictionary dict, Builder lemmaBuilder,
				Multimap<String, Wordform> wfMap) {
			GramModel gm = dict.getGramModel();
			int prdxId = gm.getGrammemNumId("Prdx");
			if (lemmaBuilder.getGrammems().get(prdxId)) {
				int predId = gm.getGrammemNumId(PRED);
				Lemma.Builder newLemma = lemmaBuilder.copy(-1);
				newLemma.getGrammems().clear();
				newLemma.getGrammems().set(predId);
				add(newLemma, copyWordforms(wfMap));
			}
			return true;
		}
	};

	private final LemmaPostProcessor subcaseProcessor = new LexemePostProcessorBase() {
		@Override
		public boolean process(MorphDictionary dict, Builder lemmaBuilder,
				Multimap<String, Wordform> wfMap) {
			GramModel gm = dict.getGramModel();
			Multimap<String, Wordform> newWfs = HashMultimap.create();
			int gen1Id = gm.getGrammemNumId(gen1);
			int gen2Id = gm.getGrammemNumId(gen2);
			int gentId = gm.getGrammemNumId(gent);
			int acc2Id = gm.getGrammemNumId(acc2);
			int accsId = gm.getGrammemNumId(accs);
			int loc1Id = gm.getGrammemNumId(loc1);
			int loc2Id = gm.getGrammemNumId(loc2);
			int loctId = gm.getGrammemNumId(loct);
			for (Map.Entry<String, Wordform> wfEntry : wfMap.entries()) {
				Wordform wf = wfEntry.getValue();
				BitSet srcGrams = wf.getGrammems();
				if (srcGrams.get(gen1Id) || srcGrams.get(gen2Id)) {
					BitSet newGrams = (BitSet) srcGrams.clone();
					newGrams.clear(gen1Id);
					newGrams.clear(gen2Id);
					newGrams.set(gentId);
					newWfs.put(wfEntry.getKey(), wf.cloneWithGrammems(newGrams));
				}
				if (srcGrams.get(acc2Id)) {
					BitSet newGrams = (BitSet) srcGrams.clone();
					newGrams.clear(acc2Id);
					newGrams.set(accsId);
					newWfs.put(wfEntry.getKey(), wf.cloneWithGrammems(newGrams));
				}
				if (srcGrams.get(loc1Id) || srcGrams.get(loc2Id)) {
					BitSet newGrams = (BitSet) srcGrams.clone();
					newGrams.clear(loc1Id);
					newGrams.clear(loc2Id);
					newGrams.set(loctId);
					newWfs.put(wfEntry.getKey(), wf.cloneWithGrammems(newGrams));
				}
			}
			// log
			for (Map.Entry<String, Wordform> newWfEntry : newWfs.entries()) {
				logModification("Wordform is added: %s %s",
						newWfEntry.getKey(), gm.toGramSet(newWfEntry.getValue().getGrammems()));
			}
			// add
			wfMap.putAll(newWfs);
			return true;
		}
	};

	private abstract class GeneratingLexemePostProcessorBase implements LemmaPostProcessor {

		private List<Lemma.Builder> genLemmas = Lists.newLinkedList();
		private List<Multimap<String, Wordform>> genWordforms = Lists.newLinkedList();

		protected void add(Lemma.Builder lb, Multimap<String, Wordform> wfs) {
			genLemmas.add(lb);
			genWordforms.add(wfs);
		}

		protected Multimap<String, Wordform> copyWordforms(Multimap<String, Wordform> src) {
			Multimap<String, Wordform> result = HashMultimap.create();
			for (Map.Entry<String, Wordform> e : src.entries()) {
				Wordform ewf = e.getValue();
				result.put(e.getKey(), new Wordform(-1, ewf.getGrammems()));
			}
			return result;
		}

		@Override
		public void dictionaryParsed(MorphDictionary _dict) {
			MorphDictionaryImpl dict = (MorphDictionaryImpl) _dict;
			GramModel gm = dict.getGramModel();
			if (genLemmas.size() != genWordforms.size()) {
				throw new IllegalStateException();
			}
			Iterator<Multimap<String, Wordform>> genWordformsIter = genWordforms.iterator();
			int maxLemmaId = dict.getLemmaMaxId();
			for (Lemma.Builder lemmaBuilder : genLemmas) {
				Multimap<String, Wordform> wfs = genWordformsIter.next();
				lemmaBuilder.setLemmaId(++maxLemmaId);
				Lemma lemma = lemmaBuilder.build();
				dict.addLemma(lemma);
				logModification("Lexeme is added: %s %s",
						lemma.getString(), gm.toGramSet(lemma.getGrammems()));
				for (String wfStr : wfs.keySet()) {
					for (Wordform wf : wfs.get(wfStr)) {
						wf = wf.cloneWithLemmaId(lemma.getId());
						dict.addWordform(wfStr, wf);
						logModification("Wordform is added: %s %s",
								wfStr, gm.toGramSet(wf.getGrammems()));
					}
				}
			}
		}
	};

	private void logModification(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}
}
