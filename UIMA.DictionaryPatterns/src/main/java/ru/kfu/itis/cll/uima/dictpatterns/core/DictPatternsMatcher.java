/**
 * 
 */
package ru.kfu.itis.cll.uima.dictpatterns.core;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static ru.kfu.itis.cll.uima.dictpatterns.core.PatternSegmentType.POSTFIX;
import static ru.kfu.itis.cll.uima.dictpatterns.core.PatternSegmentType.PREFIX;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import org.apache.commons.io.IOUtils;
import org.arabidopsis.ahocorasick.AhoCorasick;
import org.arabidopsis.ahocorasick.SearchResult;

import ru.kfu.itis.cll.dictionary.utils.CaseNormalizingTokenizer;
import ru.kfu.itis.cll.dictionary.utils.CollectPatternLineHandler;
import ru.kfu.itis.cll.dictionary.utils.Token;
import ru.kfu.itis.cll.dictionary.utils.Tokenizer;
import ru.kfu.itis.cll.dictionary.utils.TokenizerImpl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictPatternsMatcher {

	public static final String ASTERISK = CollectPatternLineHandler.WILDCARD;

	// config
	private Tokenizer tokenizer = new CaseNormalizingTokenizer(
			new TokenizerImpl(true));
	// state
	private AhoCorasick<PatternSegment> tree;

	public DictPatternsMatcher(URL patternsSourceUrl, String encoding) throws IOException {
		InputStream is = patternsSourceUrl.openStream();
		try {
			initFromStream(is, encoding);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private void initFromStream(InputStream in, String encoding) throws IOException {
		List<String> patternStrings = IOUtils.readLines(in, encoding);
		tree = new AhoCorasick<PatternSegment>();
		for (String patternStr : patternStrings) {
			if (patternStr.isEmpty()) {
				continue;
			}
			List<Token> patternTokens = tokenizer.tokenize(patternStr);
			patternTokens = handleAsteriskToken(patternTokens);
			DictionaryPattern pattern = toPattern(patternTokens);
			if (!pattern.prefix.isEmpty()) {
				addToTree(pattern.prefix, pattern, PREFIX);
			}
			if (!pattern.postfix.isEmpty()) {
				addToTree(pattern.postfix, pattern, POSTFIX);
			}
		}
		tree.prepare();
	}

	private List<Token> handleAsteriskToken(List<Token> patternTokens) {
		List<Token> result = Lists.newArrayListWithExpectedSize(patternTokens.size());
		int asteriskCounter = 0;
		for (Token srcToken : patternTokens) {
			String tString = srcToken.getString();
			if (tString.contains(ASTERISK)) {
				asteriskCounter++;
			}
			if (tString.contains(ASTERISK) && tString.length() > ASTERISK.length()) {
				int astIndex = tString.indexOf(ASTERISK);
				if (astIndex != tString.lastIndexOf(ASTERISK)) {
					throw new IllegalStateException("Token: " + tString);
				}
				String tBeforeAst = tString.substring(0, astIndex);
				String tAfterAst = tString.substring(astIndex + ASTERISK.length());
				if (!tBeforeAst.isEmpty()) {
					result.add(new Token(srcToken.getBegin(),
							srcToken.getBegin() + tBeforeAst.length(),
							tBeforeAst));
				}
				result.add(new Token(srcToken.getBegin() + tBeforeAst.length(),
						srcToken.getEnd() - tAfterAst.length(), ASTERISK));
				if (!tAfterAst.isEmpty()) {
					result.add(new Token(srcToken.getEnd() - tAfterAst.length(),
							srcToken.getEnd(), tAfterAst));
				}
			} else {
				result.add(srcToken);
			}
		}
		if (asteriskCounter != 1) {
			throw new IllegalStateException(patternTokens.toString());
		}
		return result;
	}

	private void addToTree(List<String> segment, DictionaryPattern pattern,
			PatternSegmentType segmentType) {
		tree.add(segment.toArray(new String[segment.size()]),
				new PatternSegment(segmentType, pattern));
	}

	private DictionaryPattern toPattern(List<Token> patternTokens) {
		List<String> prefixTokens = Lists.newLinkedList();
		List<String> postfixTokens = Lists.newLinkedList();
		List<String> currentList = prefixTokens;
		int asteriskCounter = 0;
		for (Token curToken : patternTokens) {
			if (ASTERISK.equals(curToken.getString())) {
				asteriskCounter++;
				currentList = postfixTokens;
			} else {
				currentList.add(curToken.getString());
			}
		}
		if (asteriskCounter != 1) {
			throw new IllegalStateException(String.format(
					"Asterisk count = %s in:\n%s", asteriskCounter, patternTokens));
		}
		return new DictionaryPattern(prefixTokens, postfixTokens);
	}

	public List<DictPatternMatch> match(String input) {
		List<DictPatternMatch> matches = Lists.newLinkedList();
		List<Token> inputTokens = tokenizer.tokenize(input);
		List<String> inputTokenStrings = Lists.transform(inputTokens,
				new Function<Token, String>() {
					@Override
					public String apply(Token input) {
						return input.getString();
					}
				});
		Iterator<SearchResult<PatternSegment>> searchResults =
				tree.search(inputTokenStrings.toArray(new String[inputTokenStrings.size()]));
		// optimize
		if (!(inputTokens instanceof RandomAccess)) {
			inputTokens = ImmutableList.copyOf(inputTokens);
		}
		Map<DictionaryPattern, Integer> prefixMap = new HashMap<DictionaryPattern, Integer>();
		while (searchResults.hasNext()) {
			SearchResult<PatternSegment> sr = searchResults.next();
			for (PatternSegment patternSeg : sr.getOutputs()) {
				DictionaryPattern pattern = patternSeg.pattern;
				switch (patternSeg.type) {
				case PREFIX: {
					if (sr.getLastIndex() >= inputTokens.size()) {
						// asterisk is not matched
						continue;
					}
					if (pattern.postfix.isEmpty()) {
						ArrayList<PatternElementSpan> match = newArrayListWithCapacity(
								pattern.getElementsCount());
						makeSegmentMatches(match, inputTokens,
								sr.getLastIndex() - pattern.prefix.size(),
								pattern.prefix);
						match.add(new PatternElementSpan(
								inputTokens.get(sr.getLastIndex()).getBegin(),
								inputTokens.get(inputTokens.size() - 1).getEnd(),
								ASTERISK));
						addMatch(matches, match, pattern);
					} else {
						// pattern with asterisk in the middle
						if (!prefixMap.containsKey(pattern)) {
							// keep only longest possible match
							prefixMap.put(pattern, sr.getLastIndex() - 1);
						}
					}
				}
					break;
				case POSTFIX: {
					int postfixFirstElemMatchIndex = sr.getLastIndex() - pattern.postfix.size();
					if (postfixFirstElemMatchIndex < 0) {
						throw new IllegalStateException();
					}
					if (postfixFirstElemMatchIndex == 0) {
						// asterisk is not matched
						continue;
					}
					if (pattern.prefix.isEmpty()) {
						List<PatternElementSpan> match = newArrayListWithCapacity(
								pattern.getElementsCount());
						// make asterisk match
						int asteriskBeginOffset = inputTokens.get(0).getBegin();
						int asteriskEndOffset = inputTokens.get(postfixFirstElemMatchIndex - 1)
								.getEnd();
						match.add(new PatternElementSpan(asteriskBeginOffset, asteriskEndOffset,
								ASTERISK));
						makeSegmentMatches(match, inputTokens, postfixFirstElemMatchIndex,
								pattern.postfix);
						addMatch(matches, match, pattern);
					} else {
						// check is prefix matched before
						Integer prefixLastElemMatchIndex = prefixMap.get(pattern);
						// must be at least one token matched by asterisk
						if (prefixLastElemMatchIndex != null &&
								postfixFirstElemMatchIndex - prefixLastElemMatchIndex > 1) {
							List<PatternElementSpan> match = newArrayListWithCapacity(
									pattern.getElementsCount());
							// make prefix match
							makeSegmentMatches(match, inputTokens,
									prefixLastElemMatchIndex - pattern.prefix.size() + 1,
									pattern.prefix);
							// make asterisk match
							int asteriskBeginOffset = inputTokens.get(prefixLastElemMatchIndex + 1)
									.getBegin();
							int asteriskEndOffset = inputTokens.get(postfixFirstElemMatchIndex - 1)
									.getEnd();
							match.add(new PatternElementSpan(asteriskBeginOffset,
									asteriskEndOffset,
									ASTERISK));
							// make postfix match
							makeSegmentMatches(match, inputTokens,
									postfixFirstElemMatchIndex, pattern.postfix);
							addMatch(matches, match, pattern);
							prefixMap.remove(pattern);
						}
					}
				}
					break;
				default:
					throw new IllegalStateException("Unknown PatternSegmentType: "
							+ patternSeg.type);
				}
			}
		}
		return matches;
	}

	private void addMatch(List<DictPatternMatch> resultList,
			List<PatternElementSpan> match,
			DictionaryPattern pattern) {
		if (pattern.getElementsCount() != match.size()) {
			throw new IllegalStateException("Pattern match size != pattern elements size");
		}
		resultList.add(new DictPatternMatch(pattern.getId(), match));
	}

	private void makeSegmentMatches(List<PatternElementSpan> resultList,
			List<Token> inputTokens, int firstMatchedTokenIndex,
			List<String> segment) {
		for (int tokenIndex = firstMatchedTokenIndex, elemIndex = 0; elemIndex < segment.size(); tokenIndex++, elemIndex++) {
			Token t = inputTokens.get(tokenIndex);
			resultList.add(
					new PatternElementSpan(t.getBegin(), t.getEnd(), segment.get(elemIndex)));
		}
	}
}

class DictionaryPattern implements Serializable {
	private static final long serialVersionUID = -3596144725614664668L;

	private static long idCounter = 1;

	final long id;
	final List<String> prefix;
	final List<String> postfix;

	public DictionaryPattern(List<String> prefix, List<String> postfix) {
		this.id = idCounter++;
		this.prefix = ImmutableList.copyOf(prefix);
		this.postfix = ImmutableList.copyOf(postfix);
		if (postfix.isEmpty() && prefix.isEmpty()) {
			throw new IllegalStateException("Empty pattern");
		}
	}

	long getId() {
		return id;
	}

	int getElementsCount() {
		return prefix.size() + postfix.size()
				// asterisk
				+ 1;
	}
}

class PatternSegment implements Serializable {
	private static final long serialVersionUID = 9042372114231119655L;

	final PatternSegmentType type;
	final DictionaryPattern pattern;

	PatternSegment(PatternSegmentType type, DictionaryPattern pattern) {
		super();
		this.type = type;
		this.pattern = pattern;
	}
}

enum PatternSegmentType {
	PREFIX, POSTFIX;
}