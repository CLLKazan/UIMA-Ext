/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.opencorpora;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;

import java.util.BitSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Logger;
import org.opencorpora.cas.Word;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;

import ru.kfu.itis.cll.uima.cas.FSUtils;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Lemma;
import ru.ksu.niimm.cll.uima.morph.opencorpora.model.Wordform;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.MorphDictionary;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.SerializedDictionaryResource;

import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class MorphologyAnnotator extends CasAnnotator_ImplBase {

	private static final String PARAM_TOKEN_TYPE = "TokenType";

	public static final String RESOURCE_KEY_DICTIONARY = "MorphDictionary";

	@ConfigurationParameter(name = PARAM_TOKEN_TYPE,
			defaultValue = "ru.kfu.cll.uima.tokenizer.fstype.Token")
	private String tokenTypeName;
	@ExternalResource(key = RESOURCE_KEY_DICTIONARY)
	private SerializedDictionaryResource dictResource;
	// derived
	private Type tokenType;
	private MorphDictionary dict;
	@SuppressWarnings("unused")
	private Logger log;

	@Override
	public void typeSystemInit(TypeSystem ts) throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
		tokenType = ts.getType(tokenTypeName);
		annotationTypeExist(tokenTypeName, tokenType);
	}

	@Override
	public void initialize(UimaContext ctx) throws ResourceInitializationException {
		super.initialize(ctx);
		log = ctx.getLogger();
		dict = dictResource.getDictionary();
		if (dict == null) {
			throw new IllegalStateException("dict is null");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(CAS cas) throws AnalysisEngineProcessException {
		try {
			process(cas.getJCas());
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

	private void process(JCas cas) throws AnalysisEngineProcessException {
		AnnotationIndex<Annotation> tokenIdx = cas.getAnnotationIndex(tokenType);
		for (Annotation token : tokenIdx) {
			String tokenStr = token.getCoveredText();
			if (proceed(tokenStr)) {
				// TODO configuration point
				// tokenizer should care about normalization 
				tokenStr = WordUtils.normalizeToDictionaryForm(tokenStr);
				List<Wordform> wfDictEntries = dict.getEntries(tokenStr);
				if (wfDictEntries != null && !wfDictEntries.isEmpty()) {
					// make word annotation
					makeWordAnnotation(cas, token, wfDictEntries);
				}
			}
		}
	}

	private void makeWordAnnotation(JCas cas, Annotation token, List<Wordform> wfDictEntries) {
		Word word = new Word(cas);
		word.setBegin(token.getBegin());
		word.setEnd(token.getEnd());
		List<org.opencorpora.cas.Wordform> casWfList = Lists.newLinkedList();
		for (Wordform wf : wfDictEntries) {
			org.opencorpora.cas.Wordform casWf = new org.opencorpora.cas.Wordform(cas);

            BitSet grammems = wf.getGrammems();
            int lemmaId = wf.getLemmaId();
            if (lemmaId > 0) {
                Lemma lemma = dict.getLemma(wf.getLemmaId());
                // set lemma id
                casWf.setLemmaId(lemma.getId());
                // set lemma norm
                casWf.setLemma(lemma.getString());
                // set pos
                casWf.setPos(dict.getPos(lemma));
                // set grammems
                grammems.or(lemma.getGrammems());
                grammems.andNot(dict.getPosBits());
            } else {
                casWf.setLemmaId(0);
                casWf.setLemma("");
                casWf.setPos("TODO");
            }
			List<String> gramSet = dict.toGramSet(grammems);
			casWf.setGrammems(FSUtils.toStringArray(cas, gramSet));

			casWfList.add(casWf);
		}
		// set wordforms
		word.setWordforms(FSUtils.toFSArray(cas, casWfList));

		word.addToIndexes();
	}

	// TODO configuration point
	private boolean proceed(String tokenStr) {
		return WordUtils.isRussianWord(tokenStr);
	}
}