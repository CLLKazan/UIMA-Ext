/**
 * 
 */
package ru.kfu.itis.cll.uima.dictpatterns.core;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DictPatternMatch {

	private long patternId;
	private List<PatternElementSpan> matchSpans;

	public DictPatternMatch(long patternId, List<PatternElementSpan> matchSpans) {
		this.patternId = patternId;
		this.matchSpans = ImmutableList.copyOf(matchSpans);
	}

	public long getPatternId() {
		return patternId;
	}

	public List<PatternElementSpan> getMatchSpans() {
		return matchSpans;
	}
}