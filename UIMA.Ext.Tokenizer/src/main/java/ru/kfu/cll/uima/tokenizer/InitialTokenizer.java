/**
 * 
 */
package ru.kfu.cll.uima.tokenizer;

import static ru.kfu.itis.cll.uima.util.AnnotatorUtils.annotationTypeExist;

import java.util.BitSet;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.CasAnnotator_ImplBase;
import org.uimafit.descriptor.ConfigurationParameter;

import ru.kfu.cll.uima.tokenizer.fstype.BREAK;
import ru.kfu.cll.uima.tokenizer.fstype.CAP;
import ru.kfu.cll.uima.tokenizer.fstype.COLON;
import ru.kfu.cll.uima.tokenizer.fstype.COMMA;
import ru.kfu.cll.uima.tokenizer.fstype.CW;
import ru.kfu.cll.uima.tokenizer.fstype.EXCLAMATION;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.PERIOD;
import ru.kfu.cll.uima.tokenizer.fstype.PM;
import ru.kfu.cll.uima.tokenizer.fstype.QUESTION;
import ru.kfu.cll.uima.tokenizer.fstype.SEMICOLON;
import ru.kfu.cll.uima.tokenizer.fstype.SPACE;
import ru.kfu.cll.uima.tokenizer.fstype.SPECIAL;
import ru.kfu.cll.uima.tokenizer.fstype.SW;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class InitialTokenizer extends CasAnnotator_ImplBase {

	@ConfigurationParameter(name = "spanType", mandatory = false)
	private String spanTypeName = "uima.tcas.DocumentAnnotation";

	// derived
	private Type spanType;

	@Override
	public void typeSystemInit(TypeSystem typeSystem) throws AnalysisEngineProcessException {
		super.typeSystemInit(typeSystem);
		spanType = typeSystem.getType(spanTypeName);
		annotationTypeExist(spanTypeName, spanType);
	}

	@Override
	public void process(CAS _cas) throws AnalysisEngineProcessException {
		JCas cas;
		try {
			cas = _cas.getJCas();
		} catch (CASException e) {
			throw new AnalysisEngineProcessException(e);
		}
		AnnotationIndex<Annotation> spanIndex = cas.getAnnotationIndex(spanType);
		FSIterator<Annotation> spanIterator = spanIndex.iterator();
		Annotation span = null;
		while (spanIterator.hasNext()) {
			span = spanIterator.next();
			tokenizeSpan(cas, span);
		}
	}

	private void tokenizeSpan(JCas cas, Annotation span) {
		String str = span.getCoveredText();
		int spanBegin = span.getBegin();
		State state = START;
		int lastStateStart = 0;
		int i = 0;
		while (i < str.length()) {
			char curCh = str.charAt(i);
			if (!state.contain(curCh)) {
				if (i - lastStateStart > 0) {
					state.createAnnotation(cas, spanBegin, str,
							lastStateStart, i);
				}
				// change state
				lastStateStart = i;
				state = getStateStartingWith(curCh);
			}
			i++;
		}
		// need this check because input string may be empty
		if (state != START) {
			state.createAnnotation(cas, spanBegin, str, lastStateStart, str.length());
		}
	}

	private State getStateStartingWith(char ch) {
		for (State candidateState : states) {
			if (candidateState.startsWith(ch)) {
				return candidateState;
			}
		}
		throw new IllegalStateException(String.format(
				"Can't determine state for character '%s'", ch));
	}

	private interface State {

		boolean contain(char ch);

		void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end);

		boolean startsWith(char ch);
	}

	private abstract class CharacterCategoryState implements State {
		private BitSet categories = new BitSet();

		CharacterCategoryState(byte... categories) {
			for (byte curCat : categories) {
				this.categories.set(curCat);
			}
		}

		@Override
		public boolean startsWith(char ch) {
			return categories.get(Character.getType(ch));
		}
	}

	private State WORD = new CharacterCategoryState(
			Character.UPPERCASE_LETTER,
			Character.LOWERCASE_LETTER,
			Character.TITLECASE_LETTER,
			Character.MODIFIER_LETTER,
			Character.OTHER_LETTER,
			Character.NON_SPACING_MARK,
			Character.ENCLOSING_MARK,
			Character.COMBINING_SPACING_MARK,
			Character.PRIVATE_USE,
			Character.SURROGATE,
			Character.MODIFIER_SYMBOL) {

		@Override
		public void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end) {
			checkOffsets(begin, end);
			int capLetters = 0;
			for (int i = begin; i < end; i++) {
				if (Character.isUpperCase(str.charAt(i))) {
					capLetters++;
				} else {
					break;
				}
			}
			Annotation anno;
			if (capLetters == 0) {
				anno = new SW(cas);
			} else if (capLetters == end - begin && capLetters > 1) {
				anno = new CAP(cas);
			} else {
				anno = new CW(cas);
			}
			postprocess(spanBegin, anno, begin, end);
		}

		@Override
		public boolean contain(char ch) {
			return startsWith(ch);
		}
	};

	private State NUM = new CharacterCategoryState(
			Character.DECIMAL_DIGIT_NUMBER,
			Character.LETTER_NUMBER,
			Character.OTHER_NUMBER) {

		@Override
		public void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end) {
			Annotation anno = new NUM(cas);
			postprocess(spanBegin, anno, begin, end);
		}

		@Override
		public boolean contain(char ch) {
			return startsWith(ch);
		}
	};

	private abstract class CharacterEnumState implements State {
		private BitSet characters = new BitSet();
		private boolean sticking;

		CharacterEnumState(boolean sticking, char... chs) {
			this.sticking = sticking;
			for (char ch : chs) {
				characters.set(ch);
			}
		}

		@Override
		public boolean startsWith(char ch) {
			return characters.get(ch);
		}

		@Override
		public boolean contain(char ch) {
			return sticking && characters.get(ch);
		}
	}

	private State BREAK = new CharacterEnumState(true, '\n', '\r') {
		@Override
		public void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end) {
			postprocess(spanBegin, new BREAK(cas), begin, end);
		}
	};

	private State SPACE = new State() {
		@Override
		public boolean startsWith(char ch) {
			return Character.isWhitespace(ch);
		}

		@Override
		public void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end) {
			postprocess(spanBegin, new SPACE(cas), begin, end);
		}

		@Override
		public boolean contain(char ch) {
			return startsWith(ch);
		}
	};

	private State PUNCTUATION = new CharacterCategoryState(
			Character.DASH_PUNCTUATION,
			Character.START_PUNCTUATION,
			Character.END_PUNCTUATION,
			Character.OTHER_PUNCTUATION,
			Character.INITIAL_QUOTE_PUNCTUATION,
			Character.FINAL_QUOTE_PUNCTUATION,
			Character.CONNECTOR_PUNCTUATION) {
		@Override
		public void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end) {
			char ch = str.charAt(begin);
			Annotation anno;
			switch (ch) {
			case ',':
				anno = new COMMA(cas);
				break;
			case '.':
				anno = new PERIOD(cas);
				break;
			case ':':
				anno = new COLON(cas);
				break;
			case ';':
				anno = new SEMICOLON(cas);
				break;
			case '?':
				anno = new QUESTION(cas);
				break;
			case '!':
				anno = new EXCLAMATION(cas);
				break;
			default:
				anno = new PM(cas);
			}
			postprocess(spanBegin, anno, begin, end);
		}

		@Override
		public boolean contain(char ch) {
			return false;
		}
	};

	private State START = new State() {
		@Override
		public boolean contain(char ch) {
			return false;
		}

		@Override
		public boolean startsWith(char ch) {
			return false;
		}

		@Override
		public void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end) {
			throw new UnsupportedOperationException();
		}

	};

	// fallback state for uncovered characters
	// MUST BE IN THE END
	private State SPECIAL = new State() {
		@Override
		public boolean contain(char ch) {
			return false;
		}

		@Override
		public void createAnnotation(JCas cas, int spanBegin, String str, int begin, int end) {
			postprocess(spanBegin, new SPECIAL(cas), begin, end);
		}

		@Override
		public boolean startsWith(char ch) {
			return true;
		}
	};

	// NOTE! Ordering of constants is CRUCIAL!
	private final ImmutableList<State> states = ImmutableList.of(
			WORD, NUM, BREAK, SPACE, PUNCTUATION, SPECIAL
			);

	private static void checkOffsets(int begin, int end) {
		if (end <= begin) {
			throw new IllegalStateException(String.format(
					"Illegal annotation offsets: %s to %s",
					begin, end));
		}
	}

	private static void postprocess(int spanBegin, Annotation anno, int begin, int end) {
		anno.setBegin(spanBegin + begin);
		anno.setEnd(spanBegin + end);
		anno.addToIndexes();
	}
}