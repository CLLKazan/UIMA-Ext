package ru.ksu.niimm.cll.uima.morph.seman;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

import ru.aot.morph.JavaMorphAPI;
import ru.aot.morph.JavaMorphAPI.Grammem;
import ru.aot.morph.JavaMorphAPI.Language;
import ru.aot.morph.JavaMorphAPI.PartOfSpeech;
import ru.aot.morph.JavaMorphAPI.WordResult;

public class SemanMorphologyAnnotator extends JCasAnnotator_ImplBase {

	private static final int REPORTING_GAP = 10;
	private static final String MESSAGE_BUNDLE_NAME = "ru.ksu.niimm.cll.uima.morph.seman.morphologyAnalyzerMessages";
	private static final String RESOURCE_JNI_LIB_MANAGER = "jniMorphLibManager";
	private static final String CONFIG_TOKEN_TYPE = "tokenTypeName";

	// init parameters
	private String tokenTypeName;

	@Override
	public void initialize(UimaContext uimaCtx)
			throws ResourceInitializationException {
		super.initialize(uimaCtx);

		try {
			uimaCtx.getResourceObject(RESOURCE_JNI_LIB_MANAGER);
		} catch (ResourceAccessException e) {
			throw new ResourceInitializationException(e);
		}

		// types
		tokenTypeName = (String) uimaCtx.getConfigParameterValue(CONFIG_TOKEN_TYPE);
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		Type tokenType = jCas.getTypeSystem().getType(tokenTypeName);
		if (tokenType == null) {
			throw new AnalysisEngineProcessException(MESSAGE_BUNDLE_NAME, "token.type.isNull",
					new String[] { tokenTypeName });
		}
		// check the parameters
		AnnotationIndex<Annotation> tokensIndex = jCas.getAnnotationIndex(tokenType);
		int tokensNum = tokensIndex.size();
		logFine("Starting morphology analyzing. Tokens num: %s", tokensNum);

		long startTime = System.currentTimeMillis();
		int curReportingThreshold = REPORTING_GAP;
		int percentageDone = 0;
		int curTokenIndex = -1;

		// TODO sorting?
		for (Annotation curToken : tokensIndex) {
			curTokenIndex++;
			String tokenString = curToken.getCoveredText();
			tokenString = normalize(tokenString);
			if (isRussianWord(tokenString)) {
				WordResult wr = JavaMorphAPI.lookupWord(Language.Russian, tokenString);
				if (wr == null) {
					continue;
				}
				for (WordResult.Paradigm para : wr.getParadigms()) {
					Paradigm paradigm = new Paradigm(jCas, curToken.getBegin(), curToken.getEnd());
					PartOfSpeech pos = para.getPartOfSpeech();
					paradigm.setPos(pos.getLabel());
					paradigm.setLemma(para.getBaseForm());
					paradigm.setGrammems(toStringArray(jCas, para.getParadigmGrammems()));
					paradigm.setParadigmId(para.getId() == null ? -1 : para.getId());
					jCas.addFsToIndexes(paradigm);

					Wordform wf = new Wordform(jCas, curToken.getBegin(), curToken.getEnd());
					wf.setGrammems(toStringArray(jCas, para.getGrammems()));
					wf.setFlexionNo(para.getFlexionNo());
					wf.setParadigm(paradigm);
					jCas.addFsToIndexes(wf);
				}
			}
			percentageDone = curTokenIndex * 100 / tokensNum;
			if (percentageDone > curReportingThreshold) {
				fireProgressChanged(percentageDone);
				curReportingThreshold += REPORTING_GAP;
			}
		}

		fireProgressChanged(100);
		logFine("PoS tagging finished. Execution time: "
				+ (System.currentTimeMillis() - startTime));

		/*document.getFeatures().put("NumberOfWords",
				new Integer(wordsNum).toString());*/
	}

	private void fireProgressChanged(int percentage) {
		logFine("%s%% complete");
	}

	private void logFine(String msg, Object... args) {
		Logger logger = getContext().getLogger();
		if (logger.isLoggable(Level.FINE)) {
			logger.log(Level.FINE, String.format(msg, args));
		}
	}

	private static StringArray toStringArray(JCas jCas, Set<Grammem> grammems) {
		if (grammems == null) {
			return jCas.getStringArray0L();
		}
		StringArray result = new StringArray(jCas, grammems.size());
		int i = 0;
		for (Grammem gr : grammems) {
			result.set(i, gr.getLabel());
			i++;
		}
		return result;
	}

	static boolean isRussianWord(String token) {
		boolean onlyNonLetters = true;
		for (int i = 0; i < token.length(); i++) {
			char ch = token.charAt(i);
			if (Character.isLetter(ch)) {
				ch = Character.toLowerCase(ch);
				if (ch < 'а' || ch > 'я') {
					return false;
				}
				onlyNonLetters = false;
			}
		}
		return !onlyNonLetters;
	}

	static String normalize(String str) {
		str = unicodeMarksPattern.matcher(str).replaceAll("");
		str = StringUtils.replaceChars(str, "ёЁ", "еЕ");
		return str;
	}

	private static Pattern unicodeMarksPattern = Pattern.compile("[\\p{Mc}\\p{Me}\\p{Mn}]");
}