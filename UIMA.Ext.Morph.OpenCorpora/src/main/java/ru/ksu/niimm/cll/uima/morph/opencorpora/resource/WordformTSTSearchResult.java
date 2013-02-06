package ru.ksu.niimm.cll.uima.morph.opencorpora.resource;

import org.opencorpora.cas.Wordform;

import java.util.Iterator;

public class WordformTSTSearchResult {
    private int matchedLength;
    private Iterator<Wordform> wordforms;

    public WordformTSTSearchResult(int matchedLength, Iterator<Wordform> wordforms) {
        this.matchedLength = matchedLength;
        this.wordforms = wordforms;
    }

    public int getMatchedLength() {
        return matchedLength;
    }

    public Iterator<Wordform> getWordforms() {
        return wordforms;
    }
}
