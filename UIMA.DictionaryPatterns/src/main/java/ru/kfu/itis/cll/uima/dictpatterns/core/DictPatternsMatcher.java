/**
 * 
 */
package ru.kfu.itis.cll.uima.dictpatterns.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.apache.commons.io.IOUtils;
import org.arabidopsis.ahocorasick.AhoCorasick;
import org.arabidopsis.ahocorasick.SearchResult;

import ru.kfu.itis.cll.dictionary.utils.CollectPatternLineHandler;
import ru.kfu.itis.cll.dictionary.utils.Token;
import ru.kfu.itis.cll.dictionary.utils.Tokenizer;

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
	private Tokenizer tokenizer = new Tokenizer(true);
	// state
	private AhoCorasick<DictionaryPattern> tree;

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
		tree = new AhoCorasick<DictionaryPattern>();
		for (String patternStr : patternStrings) {
			if (patternStr.isEmpty()) {
				continue;
			}
			List<Token> patternTokens = tokenizer.tokenize(patternStr);
			DictionaryPattern pattern = toPattern(patternTokens);
			List<String> patternMainSeq = pattern.prefix.isEmpty() ?
					pattern.postfix : pattern.prefix;
			tree.add(patternMainSeq.toArray(new String[patternMainSeq.size()]), pattern);
		}
		tree.prepare();
	}

	private DictionaryPattern toPattern(List<Token> patternTokens) {
		List<String> prefixTokens = Lists.newLinkedList();
		List<String> postfixTokens = Lists.newLinkedList();
		List<String> currentList = prefixTokens;
		for (Token curToken : patternTokens) {
			if (ASTERISK.equals(curToken)) {
				currentList = postfixTokens;
			} else {
				currentList.add(curToken.getString());
			}
		}
		return new DictionaryPattern(prefixTokens, postfixTokens);
	}

	public List<PatternElementSpan[]> match(String input) {
		List<PatternElementSpan[]> matches = Lists.newLinkedList();
		List<Token> inputTokens = tokenizer.tokenize(input);
		List<String> inputTokenStrings = Lists.transform(inputTokens,
				new Function<Token, String>() {
					@Override
					public String apply(Token input) {
						return input.getString();
					}
				});
		Iterator<SearchResult<DictionaryPattern>> searchResults =
				tree.search(inputTokenStrings.toArray(new String[inputTokenStrings.size()]));
		// optimize
		if (!(inputTokens instanceof RandomAccess)) {
			inputTokens = ImmutableList.copyOf(inputTokens);
		}
		while (searchResults.hasNext()) {
			SearchResult<DictionaryPattern> sr = searchResults.next();
			for (DictionaryPattern pattern : sr.getOutputs()) {
				boolean prefixMatched = !pattern.prefix.isEmpty();
				if (prefixMatched) {
					if (sr.getLastIndex() >= inputTokens.size()) {
						// asterisk is not matched
						continue;
					}
					if (pattern.postfix.isEmpty()) {
						PatternElementSpan[] match = new PatternElementSpan[pattern
								.getElementsCount()];
						for (int matchTokenIndex = sr.getLastIndex() - pattern.prefix.size(), matchIndex = 0; matchTokenIndex < sr
								.getLastIndex(); matchTokenIndex++, matchIndex++) {
							Token matchToken = inputTokens.get(matchTokenIndex);
							match[matchIndex] = new PatternElementSpan(
									matchToken.getBegin(), matchToken.getEnd(),
									pattern.prefix.get(matchIndex));
						}
						match[match.length - 1] = new PatternElementSpan(
								inputTokens.get(sr.getLastIndex()).getBegin(),
								inputTokens.get(inputTokens.size() - 1).getEnd(),
								ASTERISK);
						matches.add(match);
					} else {
						// pattern with asterisk in the middle 
						// TODO
					}
				} else {
					// TODO
				}
			}
		}
		return matches;
	}

	public static void main(String[] args) throws IOException {
		File file = new File(args[0]);
		if (!file.isFile()) {
			throw new IllegalStateException("Dictionary file is not exist");
		}
		DictPatternsMatcher matcher = new DictPatternsMatcher(file.toURI().toURL(), "utf-8");
	}
}

class DictionaryPattern implements Serializable {
	private static final long serialVersionUID = -3596144725614664668L;

	final List<String> prefix;
	final List<String> postfix;

	public DictionaryPattern(List<String> prefix, List<String> postfix) {
		super();
		this.prefix = ImmutableList.copyOf(prefix);
		this.postfix = ImmutableList.copyOf(postfix);
		if (postfix.isEmpty() && prefix.isEmpty()) {
			throw new IllegalStateException("Empty pattern");
		}
	}

	int getElementsCount() {
		return prefix.size() + postfix.size()
				// asterisk
				+ 1;
	}
}