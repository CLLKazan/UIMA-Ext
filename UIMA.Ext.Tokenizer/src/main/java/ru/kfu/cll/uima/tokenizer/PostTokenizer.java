/**
 * 
 */
package ru.kfu.cll.uima.tokenizer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.PM;
import ru.kfu.cll.uima.tokenizer.fstype.SPECIAL;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.TokenBase;
import ru.kfu.cll.uima.tokenizer.fstype.W;
import ru.kfu.cll.uima.tokenizer.fstype.WhiteSpace;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class PostTokenizer extends JCasAnnotator_ImplBase {

	// per-CAS state
	private Map<AnnotationFS, Collection<? extends AnnotationFS>> mergedMap;
	private Type wordType;
	private Type numType;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		CAS cas = jCas.getCas();
		mergedMap = Maps.newHashMap();
		wordType = jCas.getCasType(W.type);
		numType = jCas.getCasType(NUM.type);
		try {
			AnnotationIndex<Annotation> tokenBases = jCas.getAnnotationIndex(TokenBase.typeIndexID);
			// sequence of tokens that does not contain whitespace
			List<Token> curTokenSeq = Lists.newLinkedList();
			for (Annotation tokenBase : tokenBases) {
				if (tokenBase instanceof WhiteSpace) {
					handle(cas, ImmutableList.copyOf(curTokenSeq));
					curTokenSeq.clear();
				} else {
					// it's Token
					curTokenSeq.add((Token) tokenBase);
				}
			}
			// handle last seq
			handle(cas, ImmutableList.copyOf(curTokenSeq));
			curTokenSeq.clear();
			// index/unindex
			Set<String> mergedTokenStrings = Sets.newHashSet();
			for (Map.Entry<AnnotationFS, Collection<? extends AnnotationFS>> entry : mergedMap
					.entrySet()) {
				jCas.addFsToIndexes(entry.getKey());
				mergedTokenStrings.add(entry.getKey().getCoveredText());
				for (AnnotationFS anno : entry.getValue()) {
					jCas.removeFsFromIndexes(anno);
				}
			}
			getLogger().debug("Merged tokens: " + mergedTokenStrings);
		} finally {
			mergedMap.clear();
		}
	}

	private boolean handle(CAS cas, List<Token> tokens) {
		if (tokens.size() <= 1) {
			return false;
		} else if (tokens.size() == 2) {
			// check abbreviation dictionary
			if (isWord(tokens.get(0)) && isDot(tokens.get(1))
					&& isAbbreviation(getCoveredText(tokens))) {
				makeAnnotation(cas, tokens.get(0).getType(), tokens);
				return true;
			}
			if (!hasPMOrSpecial(tokens)) {
				makeAnnotation(cas,
						isWord(tokens.get(0)) ? tokens.get(0).getType() : wordType,
						tokens);
			}
		} else if (tokens.size() == 3) {
			Token t0 = tokens.get(0);
			Token t1 = tokens.get(1);
			Token t2 = tokens.get(2);
			if (isPossibleInnerPM(t1) && hasWord(t0, t2)) {
				makeAnnotation(cas, isWord(t0) ? t0.getType() : wordType, tokens);
				return true;
			}
			// TODO may be RANGE is better as target type, e.g. "12-14" 
			if (isNumInternalPM(t1) && isNum(t0) && isNum(t2)) {
				makeAnnotation(cas, numType, tokens);
			}
		} else {
			// tokens size >= 4
			LinkedList<Token> cleaned = Lists.newLinkedList(tokens);
			while (!cleaned.isEmpty() && isPMOrSpecial(cleaned.getFirst())) {
				cleaned.removeFirst();
			}
			while (!cleaned.isEmpty() && isPMOrSpecial(cleaned.getLast())) {
				cleaned.removeLast();
			}
			return handle(cas, cleaned);
		}
		return false;
	}

	private static final Set<String> abbreviations = ImmutableSet.of("г.");

	// TODO use external dictionary
	private boolean isAbbreviation(String str) {
		return abbreviations.contains(str);
	}

	private static final Set<String> POSSIBLE_INNER_PM = ImmutableSet.of("'", "-", "`");

	private boolean isPossibleInnerPM(Token tkn) {
		return POSSIBLE_INNER_PM.contains(tkn.getCoveredText());
	}

	private boolean isPMOrSpecial(Token tkn) {
		return tkn instanceof PM || tkn instanceof SPECIAL;
	}

	private boolean hasWord(Token... tkns) {
		for (Token tkn : tkns) {
			if (isWord(tkn)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasPMOrSpecial(Iterable<Token> tkns) {
		for (Token tkn : tkns) {
			if (isPMOrSpecial(tkn)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private boolean isHyphen(Token tkn) {
		return "-".equals(tkn.getCoveredText());
	}

	private boolean isDot(Token tkn) {
		return ".".equals(tkn.getCoveredText());
	}

	private static final Set<String> NUM_INTERNAL_PM = ImmutableSet.of(",", ".", "-");

	private boolean isNumInternalPM(Token tkn) {
		return NUM_INTERNAL_PM.contains((tkn.getCoveredText()));
	}

	private boolean isWord(Token tkn) {
		return tkn instanceof W;
	}

	private boolean isNum(Token tkn) {
		return tkn instanceof NUM;
	}

	private String getCoveredText(Iterable<? extends AnnotationFS> iter) {
		StringBuilder sb = new StringBuilder();
		for (AnnotationFS anno : iter) {
			sb.append(anno.getCoveredText());
		}
		return sb.toString();
	}

	private void makeAnnotation(CAS cas, Type targetType, List<? extends AnnotationFS> rangeAnnos) {
		int begin = rangeAnnos.get(0).getBegin();
		int end = rangeAnnos.get(rangeAnnos.size() - 1).getEnd();
		mergedMap.put(cas.createAnnotation(targetType, begin, end), rangeAnnos);
	}
}